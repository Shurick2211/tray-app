package com.nimko.trayapp.i18n

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class Translator(private val messageSource: MessageSource) {
    fun get(key: String, locale: Locale = Locale.getDefault()): String {
        return messageSource.getMessage(key, null, locale)
    }

    fun get(key: String, args: Array<Any>, locale: Locale = Locale.getDefault()): String {
        return messageSource.getMessage(key, args, locale)
    }
}