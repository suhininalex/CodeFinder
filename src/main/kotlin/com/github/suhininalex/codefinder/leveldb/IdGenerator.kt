package com.github.suhininalex.codefinder.leveldb

import com.intellij.util.containers.ContainerUtil

val GlobalGenerator = IdGenerator()

class IdGenerator(private val isSafe: Boolean = true) {
    private val existedHashes: MutableSet<Int> = ContainerUtil.newConcurrentSet()

    private fun check(hash: Int){
        val succeed = existedHashes.add(hash)
        if (! succeed) {
            throw IllegalArgumentException("Hash [$hash] already exists already exists.")
        }
    }

    fun idFrom(input: ByteArray): Int {
        val hash = input.contentHashCode()
        if (isSafe) check(hash)
        return hash
    }

    fun idFrom(input: String): Int {
        return idFrom(input.toByteArray())
    }
}