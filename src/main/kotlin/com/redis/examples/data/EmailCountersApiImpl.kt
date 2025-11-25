package com.redis.examples.data

import com.redis.examples.redis.CommandArgsFactory
import com.redis.examples.redis.HExpire
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.output.ByteArrayOutput
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
            logger.info("My connection:$connection")
            val nativeConnection = connection.getNativeConnection()
            logger.info("My native connection:$nativeConnection")
            nativeConnection.setAutoFlushCommands(false)

            nativeConnection.hset(keyByteArray, domain.toByteArray(), newValue.toString().toByteArray())

            val args = commandArgsFactory.createCommand(keyByteArray, domain, 30)
            nativeConnection.dispatch(hExpire, ByteArrayOutput(ByteArrayCodec.INSTANCE), args)

            nativeConnection.flushCommands()
            logger.info("Завершили pipeline с командами HSET и HEXPIRE")
        } catch (e: Exception) {
            logger.error("Ошибка при выполнении команд Redis", e)
        }
    }

    private companion object {
        const val COUNTER_KEY = "email:counters"
    }
}