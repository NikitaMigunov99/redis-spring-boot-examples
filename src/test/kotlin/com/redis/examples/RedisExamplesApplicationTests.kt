package com.redis.examples

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate


@SpringBootTest
class RedisExamplesApplicationTests {

	@Autowired
	private lateinit var redisTemplate: RedisTemplate<String, String>

	@Test
	fun contextLoads() {
		val randomNumber = (1..500).random()
		redisTemplate.opsForValue().set("my-key-$randomNumber", randomNumber.toString())
	}

}
