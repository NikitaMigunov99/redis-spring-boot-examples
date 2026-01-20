package com.redis.examples.models

data class RedisHashSetArguments(
    val key: ByteArray,
    val field: ByteArray,
    val value: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RedisHashSetArguments

        if (!key.contentEquals(other.key)) return false
        if (!field.contentEquals(other.field)) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.contentHashCode()
        result = 31 * result + field.contentHashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}
