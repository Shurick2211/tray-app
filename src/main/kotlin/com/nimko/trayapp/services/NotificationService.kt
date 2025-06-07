package com.nimko.trayapp.services

import org.springframework.stereotype.Service

@Service
class NotificationService:Noticeable {
    override fun notification(title: String, message: String) {
        val osName = System.getProperty("os.name").lowercase()
        when {
            osName.contains("win") -> NotificationWindows().notification(title, message)
            osName.contains("mac") -> NotificationMac().notification(title, message)
            else -> NotificationGnome().notification(title, message)
        }
    }
}