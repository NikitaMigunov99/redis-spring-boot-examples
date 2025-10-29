package com.redis.examples

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RedisExamplesApplication

fun main(args: Array<String>) {
	runApplication<RedisExamplesApplication>(*args)
}
