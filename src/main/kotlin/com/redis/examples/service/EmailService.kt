package com.redis.examples.service

import com.redis.examples.data.EmailCountersApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Thread.sleep


@Service
class EmailService(
    private val countersApi: EmailCountersApi
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun process(domain: String) {
        val counterValue = countersApi.getCounter(domain) ?: 0

        if (counterValue > 2) {
            countersApi.setAndExpire(domain, 0)
            logger.info("Обнулили счётчик для домена $domain.")
            return
        }

        sleep(1_000L)

        countersApi.setAndExpire(domain, counterValue + 1)
    }

    fun getCounter(domain: String): Int {
        return countersApi.getCounter(domain) ?: 0
    }

//    private fun setAndExpire(domain: String, newValue: Int) {
//        try {
//            logger.info("Выполняем обновление счётчика для домена $domain c новым значением $newValue.")
//            redisTemplate.opsForHash<String, String>().put(COUNTER_KEY, domain, newValue.toString())
//            logger.info("Выполняем установку времени жизни для домена $domain.")
//            redisTemplate.execute { connection: RedisConnection ->
//                // HEXPIRE key field seconds
//                val rawKey = redisTemplate.stringSerializer.serialize(COUNTER_KEY)
//                val rawField = redisTemplate.stringSerializer.serialize(domain)
//
//                // Build raw command
//                connection.execute("HEXPIRE", rawKey, rawField, "5".toByteArray())
//                null
//            }
//        } catch (ex: Exception) {
//            logger.error("Ошибка при выполнении обновления счётчика", ex)
//        }
//
//    }

}