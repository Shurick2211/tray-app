package com.nimko.trayapp.utils

import com.nimko.trayapp.model.PostEntity
import org.apache.commons.collections.CollectionUtils
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

fun toInstant(date: LocalDateTime): Instant {
    return date.toInstant(ZoneOffset.UTC)
}

fun toLocalDateTime(date: LocalDate, hh: Int, mm: Int): LocalDateTime {
    return date.atTime(hh, mm)
}


fun paramsToInstant(date: LocalDate, hh: Int, mm: Int): Instant {
    return  toInstant(toLocalDateTime(date, hh, mm))
}

fun dayShortName(dayValue: Int): String {
    val day = DayOfWeek.of(dayValue)
    return day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
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

fun dateString(post:PostEntity):String {
    val date =
    post.date?.let {d -> d.toString() } ?: if (CollectionUtils.isEmpty(post.daysOfWeek)) {
        "${post.hours}h ${post.minutes}min"
    } else {
        "${ String.format("%02d", post.hours)}:${ String.format("%02d", post.minutes)}\n${
            post.daysOfWeek.joinToString(", ") {
                dayShortName(
                    it
                )
            }
        }"
    }
    return date
}