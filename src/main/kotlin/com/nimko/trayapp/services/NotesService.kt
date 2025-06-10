package com.nimko.trayapp.services

import com.nimko.trayapp.model.NotesEntity
import com.nimko.trayapp.repository.NotesEntityRepo
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NotesService(
    private val notesRepo: NotesEntityRepo
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun findAll(): List<NotesEntity> {
        return notesRepo.findAll()
    }

    fun findById(id: Long): NotesEntity? {
        return notesRepo.findByIdOrNull(id)
    }

    fun saveOrUpdate(post: NotesEntity): NotesEntity {
        return notesRepo.save(post)
    }

    fun deleteById(id: Long) {
        notesRepo.deleteById(id)
    }
}