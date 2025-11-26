package com.redis.examples.redis

import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.output.ByteArrayOutput
import java.nio.ByteBuffer

class SafeByteArrayOutput<K, V>(
    codec: RedisCodec<K, V>
) : ByteArrayOutput<K, V>(codec) {

    /**
     * Переопределяем метод set(long), чтобы не выбрасывать исключение.
     * Преобразуем long в строку, затем в байты — как это делает Redis при возврате чисел.
     */
    override fun set(integer: Long) {
        val str = integer.toString()
        val bytes = str.toByteArray()
        set(ByteBuffer.wrap(bytes))
    }
}