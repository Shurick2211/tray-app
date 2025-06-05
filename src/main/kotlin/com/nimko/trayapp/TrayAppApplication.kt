package com.nimko.trayapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrayAppApplication

fun main(args: Array<String>) {
    runApplication<TrayAppApplication>(*args)
}
