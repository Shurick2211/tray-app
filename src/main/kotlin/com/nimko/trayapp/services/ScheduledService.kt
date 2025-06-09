package com.nimko.trayapp.services

import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.paramsToInstant
import org.apache.commons.collections.CollectionUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
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
        val localNow = LocalDate.now()

        for (post in posts) {
            val postId = post.id ?: continue
            val notifyByTime = post.active && (post.date?.isBefore(now) == true ||
                    (CollectionUtils.isNotEmpty(post.daysOfWeek) &&
                            post.daysOfWeek!!.contains(localNow.dayOfWeek.value) &&
                            paramsToInstant(localNow, post.hours, post.minutes).isBefore(now)
                            ))
            val isPeriodic =  post.active && post.date == null && post.minutes > 0

            if (notifyByTime) {
                notificationService.notification(post.text ?: "(no message)")
                log.info("🔔 Sent scheduled notification for post id=$postId")
                if(post.date != null) {
                    post.active = false
                    postService.saveOrUpdate(post)
                }
                continue
            }

            if (isPeriodic) {
                val intervalMinutes = post.hours * 60 + post.minutes
                val lastNotified = lastNotifiedMap[postId]
                val shouldNotify = (lastNotified == null ||
                        Duration.between(lastNotified, now).toMinutes() >= intervalMinutes) &&
                        CollectionUtils.isEmpty(post.daysOfWeek)

                if (shouldNotify) {
                    notificationService.notification(post.text ?: "(no message)")
                    lastNotifiedMap[postId] = now
                    log.info("🔁 Sent periodic notification for post id=$postId")
                }
            }
        }
    }
}
