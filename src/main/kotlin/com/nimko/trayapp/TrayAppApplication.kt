package com.nimko.trayapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@SpringBootApplication
@EnableScheduling
class TrayAppApplication

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "false")
    runApplication<TrayAppApplication>(*args)
}
