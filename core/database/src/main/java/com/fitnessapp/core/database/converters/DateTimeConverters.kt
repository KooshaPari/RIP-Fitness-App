package com.fitnessapp.core.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Room database type converters for date and time objects.
 * 
 * Handles conversion between Kotlin's java.time objects and
 * SQLite's string storage format for dates, times, and timestamps.
 */
class DateTimeConverters {

    companion object {
        private val localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        private val localTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
        private val localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(localDateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, localDateFormatter) }
    }

    // LocalTime converters
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(localTimeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, localTimeFormatter) }
    }

    // LocalDateTime converters
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(localDateTimeFormatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, localDateTimeFormatter) }
    }
}