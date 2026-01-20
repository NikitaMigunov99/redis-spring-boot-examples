package com.redis.examples.data

import com.redis.examples.redis.CommandArgsFactory
import com.redis.examples.redis.HExpire
import com.redis.examples.redis.SafeByteArrayOutput
import io.lettuce.core.api.sync.RedisCommands
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
                // HSET key field value
                connection.hSet(key, field, value)

                val commands = connection.nativeConnection as RedisCommands<ByteArray, ByteArray>

                // HEXPIRE key seconds FIELDS 1 field

                val hexpireResult = commands.dispatch(hExpire, SafeByteArrayOutput(ByteArrayCodec.INSTANCE), args)
                logger.info("Result of HEXPIRE: " + hexpireResult.decodeToString())
                null
            }
        } catch (e: Exception) {
            logger.error("Ошибка при выполнении команд Redis", e)
        }
    }

    private companion object {
        const val COUNTER_KEY = "email:counters"
    }
}