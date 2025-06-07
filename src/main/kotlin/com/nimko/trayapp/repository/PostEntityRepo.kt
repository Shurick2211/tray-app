package com.nimko.trayapp.repository

import com.nimko.trayapp.model.PostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PostEntityRepo: JpaRepository<PostEntity, Long> {
}