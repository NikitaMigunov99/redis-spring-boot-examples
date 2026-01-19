package com.redis.examples.controller

import com.redis.examples.service.EmailService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class TestController(private val emailService: EmailService) {

    private val domains = listOf(
        "gmail.com", "yahoo.com", "hotmail.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "yahoo.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "hotmail.com", "example.com", "outlook.com"
    )

    @PostMapping("/run")
    fun runTenTasks() {
        repeat(15) { index ->
            CompletableFuture.runAsync {
                emailService.process(domains[index])
            }
        }
    }

    @PostMapping("/set/{domain}")
    fun setDomain(@PathVariable domain: String) {
        emailService.process(domain)
    }

    @GetMapping("/get/{domain}")
    fun getByDomain(@PathVariable domain: String) : Int {
        return emailService.getCounter(domain)
    }
}