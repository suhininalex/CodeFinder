package com.github.suhininalex.codefinder.leveldb

import com.intellij.util.containers.ContainerUtil

val GlobalGenerator = IdGenerator()

class IdGenerator {
    private val existedHashes: MutableSet<Int> = ContainerUtil.newConcurrentSet()

    fun idFrom(input: ByteArray): Int {
        val hash = input.contentHashCode()
        val succeed = existedHashes.add(hash)
        if (succeed) {
            return hash
        } else {
            throw IllegalArgumentException("Id for input ${input.toList()} already exists.")
        }
    }

    fun idFrom(input: String): Int {
        return idFrom(input.toByteArray())
    }
}