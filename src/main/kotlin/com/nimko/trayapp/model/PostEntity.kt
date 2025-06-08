package com.nimko.trayapp.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
data class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var date: Instant? = null,
    var text: String? = null,
    var hours: Int = 0,
    var minutes: Int = 1,
    var active: Boolean = true,
    @JdbcTypeCode(SqlTypes.ARRAY)
    var daysOfWeek: MutableList<Int> = mutableListOf(),
    @UpdateTimestamp
    var lastUpdate: Instant? = null,
)
