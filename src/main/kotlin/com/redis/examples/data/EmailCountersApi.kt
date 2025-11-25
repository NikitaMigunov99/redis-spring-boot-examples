package com.redis.examples.data

interface EmailCountersApi {

    fun getCounter(domain: String): Int?

    fun setValue(domain: String, newValue: Int)

    fun setAndExpire(domain: String, newValue: Int)
}