package com.redis.examples.service

import com.redis.examples.data.EmailCountersApi
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Thread.sleep


@Service
class EmailService(
    private val countersApi: EmailCountersApi,
    private val registry: MeterRegistry
) {

    private val sendEmailTimer = Timer.builder("email.process.sending")
        .description("Time spent sending an email")
        .register(registry);

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun process(domain: String) {
        val start = System.nanoTime()
        val totalTime = Timer.start(registry)
        try {

            val counterValue = countersApi.getCounter(domain) ?: 0

            if (counterValue > 7) {
                countersApi.setValue(domain, 0)
                logger.info("Обнулили счётчик для домена $domain.")
            } else {
                val sendingTime = Timer.start(registry)
                sleep(200) // mock of email sending
                sendingTime.stop(sendEmailTimer)
                countersApi.setAndExpire(domain, counterValue + 1)
            }
            totalTime.stop(Timer.builder("email.process.total")
                .tag("success", "true")
                .register(registry))
        } catch (ex: Exception) {
            logger.error("Ошибка при выполнении обновления счётчика", ex)
            totalTime.stop(Timer.builder("email.process.total")
                .tag("success", "false")
                .tag("exception", ex.javaClass.simpleName)
                .register(registry))
        } finally {
            val durationMs = (System.nanoTime() - start) / 1_000_000
            logger.info("Время выполнения: $durationMs мс.")
        }
    }

    fun setValue(domain: String, value: Int) {
        countersApi.setValue(domain, value)
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