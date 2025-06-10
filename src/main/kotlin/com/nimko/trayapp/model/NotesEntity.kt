package com.nimko.trayapp.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
data class NotesEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var text: String? = null,
    @UpdateTimestamp
    var lastUpdate: Instant? = null,
)
