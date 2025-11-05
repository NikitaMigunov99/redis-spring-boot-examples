package com.redis.examples

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate


@SpringBootTest
class RedisExamplesApplicationTests {

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Test
    fun contextLoads() {
        val randomNumber = (1..500).random()
        println("Random number is $randomNumber")
        redisTemplate.opsForValue().set("my:number:test", randomNumber)
        redisTemplate.opsForValue().set("my:template:test", HTML_EXAMPLE)

        val field = "key-for-$randomNumber"
        redisTemplate.opsForHash<String, Int>().put("my:hash:number:test", field, randomNumber)
        redisTemplate.opsForHash<String, String>().put("my:hash:template:test", "template", HTML_EXAMPLE)

        val myNumber = redisTemplate.opsForValue().get("my:number:test") as? Int
        println("My number is $myNumber")
        val template = redisTemplate.opsForValue().get("my:template:test") as? String
        println("My template is $template")

        val myNumberHash = redisTemplate.opsForHash<String, Int>().get("my:hash:number:test", field)
        println("My number is from hash $myNumberHash")
        val templateHash = redisTemplate.opsForHash<String, String>().get("my:hash:template:test", "template")
        println("My template is from hash $templateHash")
    }

    private companion object {
        const val HTML_EXAMPLE = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Простая страница</title></head><body><h1>Привет!</h1><p>Это простая HTML-страничка.</p></body></html>"
    }

}
