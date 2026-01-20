package com.redis.examples.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class EmailServiceScheduler(private val emailService: EmailService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val domains = listOf(
        "gmail.com", "yahoo.com", "hotmail.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "yahoo.com", "example.com", "outlook.com",
        "gmail.com", "gmail.com", "hotmail.com", "example.com", "outlook.com"
    )

    private val counter = AtomicInteger(0)

    @Scheduled(fixedDelay = 1000) // Каждую секунду
    fun runTaskByScheduler() {
        val index = counter.get()
        val domain = domains[index]

        try {
            logger.info("Обработка домена: $domain (вызов #$index)")
            emailService.process(domain)
            logger.info("Обработан домен: $domain (вызов #$index)")
        } catch (e: Exception) {
            logger.error("Ошибка при обработке домена: $domain", e)
        } finally {
            counter.incrementAndGet()
        }

        // Обнуляем счётчик, если достигли последнего индекса
        if (index == domains.size) {
            counter.set(0)
        }
    }
}