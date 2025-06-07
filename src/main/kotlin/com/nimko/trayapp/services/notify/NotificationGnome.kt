package com.nimko.trayapp.services.notify

import org.springframework.stereotype.Service

@Service
class NotificationGnome : Noticeable {
    override fun notification(title: String, message: String) {
        Runtime.getRuntime().exec(arrayOf("notify-send", title, message))
    }
}