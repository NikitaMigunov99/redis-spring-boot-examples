package com.redis.examples

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication // 89.104.65.8 kdbZ2ZRtgON2JMxf
open class RedisExamplesApplication

fun main(args: Array<String>) {
	runApplication<RedisExamplesApplication>(*args)
}
