package com.redis.examples.redis

import io.lettuce.core.AbstractRedisClient
import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.resource.ClientResources
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConverters
import java.util.function.Consumer

class SentinelAuthLettuceConnectionFactory(
    private val sentinelConfig: RedisSentinelConfiguration,
    private val sentinelUsername: String,
    private val sentinelPassword: String,
    clientConfig: LettuceClientConfiguration
) : LettuceConnectionFactory(sentinelConfig, clientConfig) {

    override fun createClient(): AbstractRedisClient {
        if (isRedisSentinelAware) {
            val redisURI = getSentinelRedisURI()
            val redisClient = clientConfiguration.clientResources //
                .map { clientResources: ClientResources -> RedisClient.create(clientResources, redisURI) } //
                .orElseGet { RedisClient.create(redisURI) }

            clientConfiguration.clientOptions.ifPresent { clientOptions: ClientOptions ->
                redisClient.options = clientOptions
            }
            return redisClient
        }
        return super.createClient()
    }

    private fun getSentinelRedisURI(): RedisURI {
        val redisUri = LettuceConverters.sentinelConfigurationToRedisURI(sentinelConfig)

        applyToAll(redisUri) { it: RedisURI ->
            clientConfiguration.clientName.ifPresent { clientName: String ->
                it.clientName = clientName
            }
            it.isSsl = clientConfiguration.isUseSsl
            it.isVerifyPeer = clientConfiguration.isVerifyPeer
            it.isStartTls = clientConfiguration.isStartTls
            it.timeout = clientConfiguration.commandTimeout
        }
        redisUri.sentinels.forEach {
            it.username = sentinelUsername
            it.password = sentinelPassword.toCharArray()
        }

        redisUri.database = database

        return redisUri
    }

    private fun applyToAll(source: RedisURI, action: Consumer<RedisURI>) {
        action.accept(source)
        source.sentinels.forEach(action)
    }
}
