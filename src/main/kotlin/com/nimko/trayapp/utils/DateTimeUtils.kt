package com.nimko.trayapp.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

fun toInstant(date: LocalDateTime): Instant {
    return date.toInstant(ZoneOffset.UTC)
}

fun toLocalDateTime(date: LocalDate, hh: Int, mm: Int): LocalDateTime {
    return date.atTime(hh, mm)
}

const val DATE_TIME_FORMAT_ISO = "yyyy-MM-dd HH:mm"
val DATE_TIME_FORMATTER: DateTimeFormatter? = DateTimeFormatter.ofPattern(
    DATE_TIME_FORMAT_ISO
)

fun formatInstantToLocalDateTimeString(date: Instant?): String {
    if (Objects.isNull(date)) {
        return ""
    }
    val dateTime = date!!.atZone(ZoneId.systemDefault()).toLocalDateTime()
    return dateTime.format(DATE_TIME_FORMATTER)
}

fun instantToLocalDate(instant: Instant): LocalDate {
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}