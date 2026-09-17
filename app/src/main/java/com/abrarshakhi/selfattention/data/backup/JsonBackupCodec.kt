package com.abrarshakhi.selfattention.data.backup

import com.abrarshakhi.selfattention.domain.backup.BackupCodec
import com.abrarshakhi.selfattention.domain.backup.BackupData
import com.abrarshakhi.selfattention.domain.backup.BackupFormatException
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Course
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val CurrentVersion = 1

/**
 * Wire format. Kept separate from the domain models on purpose: adding a field to [Course] must
 * not silently change the file format, and every field here carries a default so an older or
 * hand-edited file still loads.
 */
@Serializable
private data class BackupFile(
    val version: Int = CurrentVersion,
    val exportedAt: String = "",
    val courses: List<CourseJson> = emptyList(),
    val attendance: List<AttendanceJson> = emptyList(),
)

@Serializable
private data class CourseJson(
    val id: Long = 0,
    val name: String = "",
    val code: String = "",
    /** ISO weekday numbers, Monday = 1. */
    val scheduleDays: List<Int> = emptyList(),
    val classHour: Int = 0,
    val classMinute: Int = 0,
    val classDurationMinutes: Int = 60,
    val hasReminder: Boolean = false,
    val reminderMinutesBefore: Int = 30,
)

@Serializable
private data class AttendanceJson(
    val courseId: Long = 0,
    /** ISO date, `yyyy-MM-dd`. */
    val date: String = "",
    val status: String = "",
)

class JsonBackupCodec @Inject constructor() : BackupCodec {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun encode(data: BackupData): String {
        val file = BackupFile(
            version = CurrentVersion,
            exportedAt = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
            courses = data.courses.map { course ->
                CourseJson(
                    id = course.id,
                    name = course.name,
                    code = course.code,
                    scheduleDays = course.scheduleDays.map { it.value },
                    classHour = course.classHour,
                    classMinute = course.classMinute,
                    classDurationMinutes = course.classDurationMinutes,
                    hasReminder = course.hasReminder,
                    reminderMinutesBefore = course.reminderMinutesBefore,
                )
            },
            attendance = data.attendance.map { record ->
                AttendanceJson(
                    courseId = record.courseId,
                    date = record.date.format(DateTimeFormatter.ISO_DATE),
                    status = record.status.name,
                )
            },
        )
        return json.encodeToString(file)
    }

    override fun decode(text: String): BackupData {
        if (text.isBlank()) throw BackupFormatException("That file is empty.")

        val file = try {
            json.decodeFromString<BackupFile>(text)
        } catch (e: Exception) {
            throw BackupFormatException(
                "That doesn't look like a Self Attention backup — it isn't valid JSON in the " +
                    "expected shape.",
            )
        }

        if (file.version > CurrentVersion) {
            throw BackupFormatException(
                "This backup was made by a newer version of the app (format ${file.version}). " +
                    "Update Self Attention and try again.",
            )
        }
        if (file.courses.isEmpty()) {
            throw BackupFormatException("That backup contains no courses.")
        }

        val courses = file.courses.mapIndexed { index, dto -> dto.toCourse(index) }
        val knownIds = courses.map { it.id }.toSet()
        val attendance = file.attendance.mapNotNull { it.toRecordOrNull(knownIds) }

        return BackupData(courses = courses, attendance = attendance)
    }

    private fun CourseJson.toCourse(index: Int): Course {
        // Report by position: a broken entry usually has no usable name to point at.
        val where = "Course ${index + 1}"
        if (name.isBlank()) throw BackupFormatException("$where has no name.")
        if (scheduleDays.isEmpty()) {
            throw BackupFormatException("\"$name\" has no class days.")
        }
        val days = scheduleDays.map { value ->
            if (value !in 1..7) {
                throw BackupFormatException(
                    "\"$name\" has an invalid class day ($value). Days run 1 (Monday) to 7 (Sunday).",
                )
            }
            DayOfWeek.of(value)
        }
        if (classHour !in 0..23 || classMinute !in 0..59) {
            throw BackupFormatException(
                "\"$name\" has an invalid class time ($classHour:$classMinute).",
            )
        }
        if (classDurationMinutes <= 0) {
            throw BackupFormatException("\"$name\" has an invalid class length.")
        }
        return Course(
            id = id,
            name = name.trim(),
            code = code.trim(),
            scheduleDays = days.distinct().sortedBy { it.value },
            classHour = classHour,
            classMinute = classMinute,
            classDurationMinutes = classDurationMinutes,
            hasReminder = hasReminder,
            reminderMinutesBefore = reminderMinutesBefore.coerceAtLeast(0),
        )
    }

    /**
     * Attendance is best-effort: a row that cannot be read is dropped rather than failing the
     * whole import, since the courses are the part worth rescuing.
     */
    private fun AttendanceJson.toRecordOrNull(knownCourseIds: Set<Long>): AttendanceRecord? {
        if (courseId !in knownCourseIds) return null
        val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull() ?: return null
        val parsedStatus = runCatching { AttendanceStatus.valueOf(status) }.getOrNull() ?: return null
        return AttendanceRecord(courseId = courseId, date = parsedDate, status = parsedStatus)
    }
}
