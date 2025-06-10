package com.nimko.trayapp.repository

import com.nimko.trayapp.model.NotesEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotesEntityRepo: JpaRepository<NotesEntity, Long> {
}