package com.nimko.trayapp.services.notify

interface Noticeable {
    fun notification(title: String, message: String)
}