package com.nimko.trayapp.services

import org.springframework.stereotype.Service

@Service
class NotificationGnome : Noticeable {
    override fun notification(title: String, message: String) {
        Runtime.getRuntime().exec(arrayOf("notify-send", "TrayApp", "This is a message"))
    }
}