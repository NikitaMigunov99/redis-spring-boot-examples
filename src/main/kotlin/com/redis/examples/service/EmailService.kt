package com.redis.examples.service

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.lang.Thread.sleep


@Service
class EmailService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun process(domain: String) {
        val counterOperations = redisTemplate.opsForHash<String, String>()
        val counterValue = counterOperations.get(COUNTER_KEY, domain)?.toIntOrNull() ?: 0

        if (counterValue > 5) {
            counterOperations.put(COUNTER_KEY, domain, "0")
            logger.info("Обнулили счётчик для домена $domain.")
            return
        }

        sleep(1_000L)

        setAndExpire(domain, counterValue + 1)
    }

    fun getCounter(domain: String): Int? {
        return redisTemplate.opsForHash<String, String>().get(COUNTER_KEY, domain)?.toIntOrNull()
    }

    private fun setAndExpire(domain: String, newValue: Int) {
        try {
            logger.info("Выполняем обновление счётчика для домена $domain c новым значением $newValue.")
            redisTemplate.opsForHash<String, String>().put(COUNTER_KEY, domain, newValue.toString())
            logger.info("Выполняем установку времени жизни для домена $domain.")
            redisTemplate.execute { connection: RedisConnection ->
                // HEXPIRE key field seconds
                val rawKey = redisTemplate.stringSerializer.serialize(COUNTER_KEY)
                val rawField = redisTemplate.stringSerializer.serialize(domain)

                // Build raw command
                connection.execute("HEXPIRE", rawKey, rawField, "5".toByteArray())
                null
            }
        } catch (ex: Exception) {
            logger.error("Ошибка при выполнении обновления счётчика", ex)
        }

    }

//    private fun setAndExpire(domain: String, newValue: Int) {
//        val luaScript = """
//            redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
//            redis.call('HEXPIRE', KEYS[1], ARGV[3], ARGV[1])
//            return tonumber(ARGV[2])
//        """.trimIndent()
//
//        val script = DefaultRedisScript<Int>(luaScript)
//
//        try {
//            logger.info("Выполняем скрипт для увеличения счётчика для домена $domain c новым значением $newValue.")
//            val result = redisTemplate.execute(script, listOf(COUNTER_KEY), domain, newValue, 5)
//
//            logger.info("Значение увеличено с результатом ($result)")
//        } catch (ex: Exception) {
//            logger.error("Ошибка при выполнении скрипта", ex)
//        }
//    }

    companion object {
        const val COUNTER_KEY = "email:counters"
    }
}