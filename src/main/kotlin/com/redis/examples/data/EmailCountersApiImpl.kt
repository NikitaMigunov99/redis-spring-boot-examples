package com.redis.examples.data

import com.redis.examples.redis.CommandArgsFactory
import com.redis.examples.redis.HExpire
import com.redis.examples.redis.SafeByteArrayOutput
import io.lettuce.core.RedisFuture
import io.lettuce.core.codec.ByteArrayCodec
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component


@Component
@Primary
class EmailCountersApiImpl(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val connectionFactory: LettuceConnectionFactory,
    private val commandArgsFactory: CommandArgsFactory
) : EmailCountersApi {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val keyByteArray = COUNTER_KEY.toByteArray()
    private val hExpire = HExpire()

    override fun getCounter(domain: String): Int? {
        return redisTemplate.opsForHash<String, String>().get(COUNTER_KEY, domain)?.toIntOrNull()
    }

    override fun setValue(domain: String, newValue: Int) {
        redisTemplate.opsForHash<String, String>().put(COUNTER_KEY, domain, newValue.toString())
    }

    override fun setAndExpire(domain: String, newValue: Int) {
        try {
            logger.info("Запускаем pipeline с командами HSET и HEXPIRE")
            val connection = connectionFactory.getClusterConnection() as LettuceClusterConnection
            val nativeConnection = connection.getNativeConnection()
            nativeConnection.setAutoFlushCommands(false)

            val hsetResult = nativeConnection.hset(keyByteArray, domain.toByteArray(), newValue.toString().toByteArray())

            val args = commandArgsFactory.createCommand(keyByteArray, domain, 30)
            val hexpireResult = nativeConnection.dispatch(hExpire, SafeByteArrayOutput(ByteArrayCodec.INSTANCE), args)
            val hsetSecondResult = nativeConnection.hincrby(keyByteArray, domain.toByteArray(), 10)


            nativeConnection.flushCommands()


            logger.info("Ждем завершения pipeline с командами HSET и HEXPIRE")
//            logger.info("Результат HSET: " + hsetResult.await(30, java.util.concurrent.TimeUnit.SECONDS))
//            logger.info("Результат HEXPIRE: " + hexpireResult.await(30, java.util.concurrent.TimeUnit.SECONDS))

            processRedisFutureSync(hsetResult)
            processRedisFutureForExpirationSync(hexpireResult)
            processRedisFutureSync(hsetSecondResult)

            logger.info("Завершили pipeline с командами HSET и HEXPIRE")
            logger.info("Результат при выполнении HEXPIRE: " + hexpireResult.get().decodeToString())
        } catch (e: Exception) {
            logger.error("Ошибка при выполнении команд Redis", e)
        }
    }

    private fun processRedisFutureSync(future: RedisFuture<*>) {
        try {
            val result = future.get(30, java.util.concurrent.TimeUnit.SECONDS)
            logger.info("Result of update: $result")
        } catch (e: Exception) {
            logger.error(ERROR_HSET_MESSAGE, e)
        }
    }

    private fun processRedisFutureForExpirationSync(future: RedisFuture<ByteArray>) {
        try {
            val result = future.get(30, java.util.concurrent.TimeUnit.SECONDS).decodeToString()
            if (ONE != result) {
                logger.error("The expiration time was not set/updated. Result: $result")
            }
        } catch (e: Exception) {
            logger.error(ERROR_HEXPIRE_MESSAGE, e)
        }
    }

    private companion object {
        const val COUNTER_KEY = "email:counters"
        const val ERROR_HSET_MESSAGE = "Error during HSET execution"
        const val ERROR_HEXPIRE_MESSAGE = "Error during HEXPIRE execution"
        const val ONE = "1"
    }
}