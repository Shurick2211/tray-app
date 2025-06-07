package com.nimko.trayapp.services

class NotificationMac: Noticeable {
    override fun notification(title: String, message: String) {
        Runtime.getRuntime().exec(arrayOf("osascript", "-e", "display notification \"$message\" with title \"$title\""))
    }
}