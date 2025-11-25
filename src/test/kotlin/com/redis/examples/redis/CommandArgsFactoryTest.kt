package com.redis.examples.redis

import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.protocol.CommandArgs
import org.junit.jupiter.api.Test

class CommandArgsFactoryTest {

    @Test
    fun createCommand() {
        val args = CommandArgs(ByteArrayCodec.INSTANCE)
            .addKey("my-key".toByteArray())
            .add("5")
            .add("FIELDS")
            .add("1")
            .add("mail.ru")
        println(args.toCommandString())
    }
}