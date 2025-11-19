package com.redis.examples.data

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component

@Component
open class EmailCountersApi(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val script: RedisScript<String>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getCounter(domain: String): Int? {
        return redisTemplate.opsForHash<String, String>().get(COUNTER_KEY, domain)?.toIntOrNull()
    }

    fun setValue(domain: String, newValue: Int) {
        redisTemplate.opsForHash<String, String>().put(COUNTER_KEY, domain, newValue.toString())
    }

    fun setAndExpire(domain: String, newValue: Int) {
        try {
            logger.info("Выполняем скрипт для увеличения счётчика для домена $domain c новым значением $newValue c временем жизни 5 сек.")
            val result = redisTemplate.execute(script, listOf(COUNTER_KEY), domain, newValue, 15)

            logger.info("Значение увеличено с результатом ($result)")
        } catch (ex: Exception) {
            logger.error("Ошибка при выполнении скрипта", ex)
        }
    }

    companion object {
        const val COUNTER_KEY = "email:counters"
    }

}