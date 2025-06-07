package com.nimko.trayapp.services

import com.nimko.trayapp.model.PostEntity
import com.nimko.trayapp.repository.PostEntityRepo
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postEntityRepo: PostEntityRepo
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun findAll(): List<PostEntity> {
        return postEntityRepo.findAll()
    }

    fun findById(id: Long): PostEntity? {
        return postEntityRepo.findByIdOrNull(id)
    }

    fun saveOrUpdate(post: PostEntity): PostEntity {
        return postEntityRepo.save(post)
    }

    fun deleteById(id: Long) {
        postEntityRepo.deleteById(id)
    }
}