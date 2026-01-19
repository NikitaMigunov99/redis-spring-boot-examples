package com.redis.examples.controller

import com.redis.examples.service.EmailService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors

@RestController
class TestController(private val emailService: EmailService) {

    private val domains = listOf(
        "gmail.com", "yahoo.com", "hotmail.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "yahoo.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "hotmail.com", "example.com", "outlook.com"
    )

    @PostMapping("/run")
    fun runTenTasks() {
        val executor = Executors.newFixedThreadPool(10)
        repeat(15) { index ->
            executor.submit {
                Thread.sleep((100L..500L).random()) // небольшая задержка
                emailService.process(domains[index])
            }
        }
    }
}