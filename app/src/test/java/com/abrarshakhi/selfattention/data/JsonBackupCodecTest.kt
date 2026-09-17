package com.abrarshakhi.selfattention.data

import com.abrarshakhi.selfattention.data.backup.JsonBackupCodec
import com.abrarshakhi.selfattention.domain.backup.BackupData
import com.abrarshakhi.selfattention.domain.backup.BackupFormatException
import com.abrarshakhi.selfattention.domain.model.AttendanceRecord
import com.abrarshakhi.selfattention.domain.model.AttendanceStatus
import com.abrarshakhi.selfattention.domain.model.Course
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * The import path is the one place a user hands the app an arbitrary file, so every malformed
 * shape must surface a readable message rather than an exception.
 */
class JsonBackupCodecTest {

    private val codec = JsonBackupCodec()

    @Test
    fun `round-trips courses and attendance`() {
        val data = BackupData(
            courses = listOf(buildCourse()),
            attendance = listOf(
                AttendanceRecord(
                    courseId = 1L,
                    date = LocalDate.of(2026, 9, 16),
                    status = AttendanceStatus.PRESENT,
                ),
            ),
        )

        val restored = codec.decode(codec.encode(data))

        assertEquals(1, restored.courses.size)
        assertEquals("Databases", restored.courses[0].name)
        assertEquals(listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), restored.courses[0].scheduleDays)
        assertEquals(1, restored.attendance.size)
        assertEquals(AttendanceStatus.PRESENT, restored.attendance[0].status)
        assertEquals(LocalDate.of(2026, 9, 16), restored.attendance[0].date)
    }

    @Test
    fun `rejects text that is not json`() {
        val message = failureMessage("this is definitely not json")

        assertTrue(message, message.contains("doesn't look like"))
    }

    @Test
    fun `rejects an empty file`() {
        assertTrue(failureMessage("   ").contains("empty"))
    }

    @Test
    fun `rejects valid json that is not a backup`() {
        assertTrue(failureMessage("""{"hello":"world"}""").contains("no courses"))
    }

    @Test
    fun `rejects a course with no name`() {
        val json = """{"version":1,"courses":[{"name":"","scheduleDays":[1],"classHour":9}]}"""

        assertTrue(failureMessage(json).contains("no name"))
    }

    @Test
    fun `rejects a course with no class days`() {
        val json = """{"version":1,"courses":[{"name":"Maths","scheduleDays":[],"classHour":9}]}"""

        assertTrue(failureMessage(json).contains("no class days"))
    }

    @Test
    fun `rejects an out-of-range weekday`() {
        val json = """{"version":1,"courses":[{"name":"Maths","scheduleDays":[9],"classHour":9}]}"""
        val message = failureMessage(json)

        assertTrue(message, message.contains("invalid class day"))
    }

    @Test
    fun `rejects an impossible class time`() {
        val json =
            """{"version":1,"courses":[{"name":"Maths","scheduleDays":[1],"classHour":99}]}"""

        assertTrue(failureMessage(json).contains("invalid class time"))
    }

    @Test
    fun `rejects a backup from a newer app version`() {
        val json = """{"version":99,"courses":[{"name":"Maths","scheduleDays":[1],"classHour":9}]}"""

        assertTrue(failureMessage(json).contains("newer version"))
    }

    /** Unknown keys are tolerated so a future field does not make old builds refuse the file. */
    @Test
    fun `ignores unknown fields`() {
        val json = """
            {"version":1,"somethingNew":true,
             "courses":[{"name":"Maths","scheduleDays":[1],"classHour":9,"extra":5}]}
        """.trimIndent()

        assertEquals(1, codec.decode(json).courses.size)
    }

    /** A bad attendance row is dropped; the courses are still worth importing. */
    @Test
    fun `drops unreadable attendance rows without failing the import`() {
        val json = """
            {"version":1,
             "courses":[{"id":5,"name":"Maths","scheduleDays":[1],"classHour":9}],
             "attendance":[
               {"courseId":5,"date":"not-a-date","status":"PRESENT"},
               {"courseId":5,"date":"2026-09-16","status":"NONSENSE"},
               {"courseId":999,"date":"2026-09-16","status":"PRESENT"},
               {"courseId":5,"date":"2026-09-16","status":"ABSENT"}
             ]}
        """.trimIndent()

        val data = codec.decode(json)

        assertEquals(1, data.courses.size)
        assertEquals(1, data.attendance.size)
        assertEquals(AttendanceStatus.ABSENT, data.attendance[0].status)
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun failureMessage(text: String): String =
        try {
            codec.decode(text)
            "no exception was thrown"
        } catch (e: BackupFormatException) {
            e.message.orEmpty()
        }

    private fun buildCourse() = Course(
        id = 1L,
        name = "Databases",
        code = "CS-201",
        scheduleDays = listOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
        classHour = 9,
        classMinute = 30,
        classDurationMinutes = 60,
        hasReminder = true,
        reminderMinutesBefore = 15,
        createdAt = 1_758_000_000_000L,
    )
}
