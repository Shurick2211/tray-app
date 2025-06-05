package com.nimko.trayapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrayAppApplication

fun main(args: Array<String>) {
    System.setProperty("java.awt.headless", "false")
    runApplication<TrayAppApplication>(*args)
}
