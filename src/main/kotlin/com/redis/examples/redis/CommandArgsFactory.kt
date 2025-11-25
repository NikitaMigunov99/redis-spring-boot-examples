package com.redis.examples.redis

import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.protocol.CommandArgs
import org.springframework.stereotype.Component

@Component
class CommandArgsFactory {

    fun createCommand(key: ByteArray, domain: String, ttl: Int): CommandArgs<ByteArray, ByteArray> {
        val args = CommandArgs(ByteArrayCodec.INSTANCE)
            .addKey(key)
            .add(ttl.toString())
            .add("FIELDS")
            .add("1")
            .add(domain)
        return args
    }
}