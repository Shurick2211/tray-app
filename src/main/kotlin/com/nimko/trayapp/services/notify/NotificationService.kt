package com.nimko.trayapp.services.notify

import org.apache.commons.lang3.StringUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties(prefix = "app")
class NotificationService {
    var title: String = ""

    fun notification(message: String) {
        val mess = if (StringUtils.isNotBlank(message)) {
            message
        } else {
            ""
        }
        val osName = System.getProperty("os.name").lowercase()
        val notifier = when {
            osName.contains("win") -> NotificationWindows()
            osName.contains("mac") -> NotificationMac()
            else -> NotificationGnome()
        }
        notifier.notification(title, message)
    }
}