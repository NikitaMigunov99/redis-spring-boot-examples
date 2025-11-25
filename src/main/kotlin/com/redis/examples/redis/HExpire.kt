package com.redis.examples.redis

import io.lettuce.core.protocol.ProtocolKeyword

class HExpire : ProtocolKeyword {

    private val expire = "HEXPIRE"
    private val bytes = expire.toByteArray()

    override fun getBytes(): ByteArray = bytes

    override fun name(): String = expire
}