package com.nimko.trayapp.services

import com.nimko.trayapp.services.notify.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class ScheduledService(
    private val postService: PostService,
    private val notificationService: NotificationService,
) {
    private val log = LoggerFactory.getLogger(ScheduledService::class.java)

    private val lastNotifiedMap = ConcurrentHashMap<Long, Instant>()

    @Scheduled(fixedRate = 60_000)
    fun checkPosts() {
        val now = Instant.now()
        val posts = postService.findAll()
        log.info("Found: {}", posts)

        for (post in posts) {
            val postId = post.long ?: continue
            val notifyByTime = post.active && post.date?.isBefore(now) == true
            val isPeriodic =  post.active && post.date == null && post.minutes > 0

            if (notifyByTime) {
                notificationService.notification(post.text ?: "(no message)")
                log.info("🔔 Sent scheduled notification for post id=$postId")
                post.active = false
                postService.saveOrUpdate(post)
                continue
            }

            if (isPeriodic) {
                val intervalMinutes = post.hours * 60 + post.minutes
                val lastNotified = lastNotifiedMap[postId]
                val shouldNotify = lastNotified == null ||
                        Duration.between(lastNotified, now).toMinutes() >= intervalMinutes

                if (shouldNotify) {
                    notificationService.notification(post.text ?: "(no message)")
                    lastNotifiedMap[postId] = now
                    log.info("🔁 Sent periodic notification for post id=$postId")
                }
            }
        }
    }
}
