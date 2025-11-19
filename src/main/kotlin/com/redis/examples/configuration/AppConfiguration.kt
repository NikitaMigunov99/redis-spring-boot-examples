package com.redis.examples.configuration

import io.lettuce.core.ClientOptions
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.DefaultClientResources
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableConfigurationProperties(RedisProperties::class)
open class AppConfiguration {

    @Bean
    open fun updateAndSetTTL(): RedisScript<Int> {
        val scriptSource = ClassPathResource("scripts/script-for-one-field.lua")
        return RedisScript.of(scriptSource, Int::class.java) // Specify return type
    }

    @Bean
    open fun clientResources(): ClientResources = DefaultClientResources.create()

    @Bean
    open fun clientOptions(): ClientOptions =
        ClientOptions
            .builder()
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT)
            .autoReconnect(true)
            .build()

    @Bean
    open fun lettuceConfiguration(
        clientOptions: ClientOptions,
        clientResources: ClientResources,
        redisProperties: RedisProperties
    ): LettucePoolingClientConfiguration =
        LettucePoolingClientConfiguration.builder()
            .clientName(redisProperties.username)
            .clientOptions(clientOptions)
            .clientResources(clientResources)
            .poolConfig(redisProperties.getGenericObjectPoolConfig<Any>())
            .build()

    @Bean
    open fun redisCluster(redisProperties: RedisProperties): RedisClusterConfiguration {
        val config = RedisClusterConfiguration(redisProperties.nodes)
        config.password = RedisPassword.of(redisProperties.password)
        return config
    }

    @Bean
    open fun lettuceConnectionFactory(
        clusterConfiguration: RedisClusterConfiguration,
        clientConfiguration: LettucePoolingClientConfiguration
    ): RedisConnectionFactory {
        val factory = LettuceConnectionFactory(clusterConfiguration, clientConfiguration)
        factory.afterPropertiesSet()
        return factory
    }

    @Bean
    open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericToStringSerializer(Any::class.java)

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericToStringSerializer(Any::class.java)
        template.afterPropertiesSet()
        return template
    }

}