package dev.euns.jwttemplate.domain.auth.repository

import dev.euns.jwttemplate.domain.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): List<User>


}