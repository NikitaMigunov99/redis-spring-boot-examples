package com.redis.examples.service

import com.redis.examples.models.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun example() {
        val user = User(id = "1", name = "Alice", age = 30)
        userService.saveUser(user)

        val retrieved = userService.getUser("1")
        println("My user $retrieved") // User(id=1, name=Alice, age=30)
    }
}