package com.redis.examples.service

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.lang.Thread.sleep

@Service
class EmailService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun process(domain: String) {
        val counterOperations = redisTemplate.opsForHash<String, Int>()
        val counterValue = counterOperations.get(COUNTER_KEY, domain) ?: 0

        if (counterValue > 5) {
            counterOperations.put(COUNTER_KEY, domain, 0)
            logger.info("Обнулили счётчик для домена $domain.")
            return
        }

        sleep(5_000L)

        setAndExpire(domain, counterValue + 1)
    }

    fun getCounter(domain: String): Int? {
        return redisTemplate.opsForHash<String, Int>().get(COUNTER_KEY, domain)
    }

    private fun setAndExpire(domain: String, newValue: Int) {
        val luaScript = """
            redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
            redis.call('HEXPIRE', KEYS[1], ARGV[1], 5)
            return ARGV[2]
        """.trimIndent()

        val script = DefaultRedisScript<Long>(luaScript)

        try {
            val result = redisTemplate.execute(script, listOf(COUNTER_KEY), listOf(domain, newValue))

            logger.info("Значение увеличено на единицу ($result). Поле теперь актуально ещё 5 секунд.")
        } catch (ex: Exception) {
            logger.error("Ошибка при выполнении скрипта", ex)
        }
    }

    companion object {
        const val COUNTER_KEY = "email:counters"
    }
}