package com.nimko.trayapp.services

interface Noticeable {
    fun notification(title: String, message: String)
}