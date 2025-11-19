package com.redis.examples.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.redis.examples.models.User
import io.lettuce.core.ClientOptions
import io.lettuce.core.resource.ClientResources
import io.lettuce.core.resource.DefaultClientResources
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableConfigurationProperties(RedisProperties::class)
open class AppConfiguration {

    //@Bean
    open fun objectMapper(): ObjectMapper {
        val validator: PolymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.redis.examples.models.User") // Укажите полный путь
            .build()

        return ObjectMapper().apply {
            activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL)
        }
    }

    @Bean(name = ["redisTemplateForUsers"])
    open fun redisTemplateForUsers(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, User> {
        val template = RedisTemplate<String, User>()
        template.connectionFactory = redisConnectionFactory

        val serializer = Jackson2JsonRedisSerializer(User::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer // Сериализуем объекты в JSON

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        template.afterPropertiesSet()
        return template
    }

    @Bean
    @Primary
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

    @Bean
    open fun updateAndSetTTL(): RedisScript<String> {
        val scriptSource = ClassPathResource("script/script-for-one-field.lua")
        return RedisScript.of(scriptSource, String::class.java) // Specify return type
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

}