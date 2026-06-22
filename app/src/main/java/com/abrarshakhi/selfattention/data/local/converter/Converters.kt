package com.abrarshakhi.selfattention.data.local.converter

import androidx.room.TypeConverter
import java.time.DayOfWeek

class Converters {
    @TypeConverter
    fun fromDayOfWeekList(days: List<DayOfWeek>): String =
        days.joinToString(",") { it.value.toString() }

    @TypeConverter
    fun toDayOfWeekList(value: String): List<DayOfWeek> =
        if (value.isBlank()) emptyList()
        else value.split(",").map { DayOfWeek.of(it.trim().toInt()) }
}
