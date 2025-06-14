package com.nimko.trayapp.services

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

@Service
class RandomLineService {

    private val log = LoggerFactory.getLogger(RandomLineService::class.java)
    private val lines = mutableListOf<String>()

    private val filePath = "./quote.txt"

    @PostConstruct
    fun init() {
        val path = Paths.get(filePath)
        if (Files.exists(path)) {
            lines.addAll(Files.readAllLines(path))
        } else {
            log.warn("File path $filePath does not exist")
        }
    }

    fun getRandomLine(): String? {
        if (lines.isEmpty()) return null
        val randomIndex = Random.nextInt(lines.size)
        return lines[randomIndex]
    }
}
