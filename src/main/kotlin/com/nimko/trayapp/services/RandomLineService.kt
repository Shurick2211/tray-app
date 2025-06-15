package com.nimko.trayapp.services

import com.nimko.trayapp.services.notify.NotificationService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class RandomLineService(
    private val notificationService: NotificationService
) {

    private val log = LoggerFactory.getLogger(RandomLineService::class.java)
    private val lines = mutableListOf<String>()


    @PostConstruct
    fun init() {
        val inputStream = javaClass.classLoader.getResourceAsStream("quote.txt")
        if (inputStream != null) {
            inputStream.bufferedReader().useLines { lines.addAll(it) }
        } else {
            log.warn("Resource quote.txt not found in classpath")
        }
    }

    fun getRandomLine(): String? {
        if (lines.isEmpty()) return null
        val randomIndex = Random.nextInt(lines.size)
        return lines[randomIndex]
    }

    fun notifyQuote() {
        notificationService.notification(getRandomLine()?:"not found...")
    }
}
