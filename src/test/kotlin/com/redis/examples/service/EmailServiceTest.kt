package com.redis.examples.service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private lateinit var emailService: EmailService

    @Test
    fun testService() {
        for (i in 1..5) {
            println("Processing email $i")
            emailService.process("gmail.com")

            val counter = emailService.getCounter("gmail.com")
            println("Finished processing email $i, counter: $counter")
        }
        Thread.sleep(5000)
        println("Setting value without TTL")
        emailService.setValue("gmail.com", 1000)
        val value = emailService.getCounter("gmail.com")
        println("My value $value after setting")
    }
}