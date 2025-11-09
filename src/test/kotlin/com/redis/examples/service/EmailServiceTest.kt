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
        for (i in 1..7) {
            println("Processing email $i")
            emailService.process("gmail.com")

            val counter = emailService.getCounter("gmail.com")
            println("Finished processing email $i, counter: $counter")
        }
    }
}