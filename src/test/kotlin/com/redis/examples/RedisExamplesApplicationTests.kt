package com.redis.examples

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate



@SpringBootTest
class RedisExamplesApplicationTests {

	@Autowired
	private lateinit var redisTemplate: RedisTemplate<Any, Any>

	@Test
	fun contextLoads() {
		redisTemplate.opsForValue().set("key", 55);
	}

}
