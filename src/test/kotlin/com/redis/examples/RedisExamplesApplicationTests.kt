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
    fun testTemplate() {
        val randomNumber = (1..500).random()
        println("Random number is $randomNumber")
        redisTemplate.opsForValue().set("my:number:test", randomNumber)
        redisTemplate.opsForValue().set("my:template:test", HTML_EXAMPLE)

        val field = "key-for-$randomNumber"
        redisTemplate.opsForHash<String, String>().put("my:hash:number:test", field, randomNumber.toString())
        redisTemplate.opsForHash<String, String>().put("my:hash:template:test", "template", HTML_EXAMPLE)

        val myNumberAny = redisTemplate.opsForValue().get("my:number:test")
        println("My number Object is $myNumberAny")
        val myNumber = redisTemplate.opsForValue().get("my:number:test")?.toString()?.toIntOrNull()
        println("My number is $myNumber")
        val template = redisTemplate.opsForValue().get("my:template:test") as? String
        println("My template is $template")

        val result = redisTemplate.opsForValue().increment("my:number:test")
        println("Increment result is $result")

        val myNumberAfterIncrement = redisTemplate.opsForValue().get("my:number:test")?.toString()?.toIntOrNull()
        println("My number is $myNumberAfterIncrement")

        val myNumberHash = redisTemplate.opsForHash<String, String>().get("my:hash:number:test", field)?.toIntOrNull()

        println("My number is from hash $myNumberHash")
        val templateHash = redisTemplate.opsForHash<String, String>().get("my:hash:template:test", "template")
        println("My template is from hash $templateHash")

        val nullObject = redisTemplate.opsForValue().get("null:test")
        println("My null Object is $nullObject")

        val nullHash = redisTemplate.opsForHash<String, String>().get("my:null:hash", "my:hash")
        println("My null from hash $nullHash")

        try {
            println("Try to increment null")
            val result = redisTemplate.opsForValue().increment("null:test")
            println("Try to increment null result is $result")
        } catch (e: Exception) {
            println("My null Object is not a number: $e")
        }

        try {
            println("Try to increment string")
            val result = redisTemplate.opsForValue().increment("my:template:test")
            println("Try to increment string result is $result")
        } catch (e: Exception) {
            println("My string Object is not a number: $e")
        }

        try {
            println("Set number to hash")
            redisTemplate.opsForHash<String, Int>().put("counter:map", "field",5)
            val counter = redisTemplate.opsForHash<String, String>().get("counter:map", "field")
            println("The counter is $counter")

            val counterInt = redisTemplate.opsForHash<String, Int>().get("counter:map", "field")
            println("The int counter is $counterInt")
        } catch (e: Exception) {
            println("Error with the map :$e")
        }
    }

    @Test
    fun contextLoads() {}

    private companion object {
        const val HTML_EXAMPLE = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Простая страница</title></head><body><h1>Привет!</h1><p>Это простая HTML-страничка.</p></body></html>"
    }

}
