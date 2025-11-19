package com.redis.examples.models

import java.io.Serializable

data class User(
    var id: String,
    var name: String,
    var age: Int
) : Serializable {
    constructor() : this("", "", 0)
}
