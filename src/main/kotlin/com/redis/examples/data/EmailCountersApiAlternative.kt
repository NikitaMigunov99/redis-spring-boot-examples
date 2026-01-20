package com.redis.examples.data

import com.redis.examples.redis.CommandArgsFactory
import com.redis.examples.redis.HExpire
import com.redis.examples.redis.SafeByteArrayOutput
import io.lettuce.core.api.async.BaseRedisAsyncCommands
import io.lettuce.core.codec.ByteArrayCodec
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
@Primary
class EmailCountersApiAlternative(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val commandArgsFactory: CommandArgsFactory
) : EmailCountersApi {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val keyByteArray = COUNTER_KEY.toByteArray()
    private val hExpire = HExpire()

    override fun getCounter(domain: String): Int? {
        return redisTemplate.opsForHash<String, String>().get(COUNTER_KEY, domain)?.toIntOrNull()
    }

    override fun setValue(domain: String, newValue: Int) {
        redisTemplate.opsForHash<String, Int>().put(COUNTER_KEY, domain, newValue)
    }

    override fun setAndExpire(domain: String, newValue: Int) {
        try {
            logger.info("Запускаем pipeline с командами HSET и HEXPIRE")
            val args = commandArgsFactory.createCommand(keyByteArray, domain, 30)
            val key = redisTemplate.stringSerializer.serialize("myHashKey")
            val field = redisTemplate.stringSerializer.serialize("myField")
            val value = redisTemplate.stringSerializer.serialize(newValue.toString())
            redisTemplate.executePipelined { connection: RedisConnection ->

                connection.hSet(key, field, value)

                val nativeConnection = connection.nativeConnection as BaseRedisAsyncCommands<ByteArray, ByteArray>
                nativeConnection
                    .dispatch(hExpire, SafeByteArrayOutput(ByteArrayCodec.INSTANCE), args)
                    .whenComplete { result, throwable ->
                        if (throwable != null) {
                            logger.error(ERROR_HEXPIRE_MESSAGE, throwable)
                        } else if (result != null) {
                            logger.info("Result of HEXPIRE: " + result.decodeToString())
                        }
                    }

                null
            }
        } catch (e: Exception) {
            logger.error("Ошибка при выполнении команд Redis", e)
        }
    }

    private companion object {
        const val ERROR_HEXPIRE_MESSAGE = "Error during HEXPIRE execution"
        const val COUNTER_KEY = "email:counters"
    }
}