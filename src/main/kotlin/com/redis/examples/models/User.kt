package com.redis.examples.models

import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val age: Int
) : Serializable
