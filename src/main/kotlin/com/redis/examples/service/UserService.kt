package com.redis.examples.service

import com.redis.examples.models.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class UserService(@Qualifier("redisTemplateForUsers") private val userRedisTemplate: RedisTemplate<String, User>) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getUser(id: String): User? =
        try {
            userRedisTemplate.opsForValue().get(USER_KEY + id) as User?
        } catch (e: Exception) {
            logger.error("Error getting user", e)
            null
        }

    fun saveUser(user: User) {
        try {
            userRedisTemplate.opsForValue().set(USER_KEY + user.id, user)
        } catch (e: Exception) {
            logger.error("Error saving user", e)
        }
    }

    private companion object {
        const val USER_KEY = "user"
    }
}