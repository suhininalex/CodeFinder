package com.github.suhininalex.codefinder.leveldb

import org.apache.hadoop.util.hash.Hash

class Hasher(hashes: Int = 2) {
    private val function = Hash.getInstance(Hash.MURMUR_HASH)

    private val initValues = (1..hashes)

    fun hash(input: ByteArray): ByteArray {
        val hashes = initValues.map { initValue -> function.hash(input, initValue) }
        return createBytes { out -> hashes.forEach { out.writeInt(it) } }
    }

    fun hash(input: String): ByteArray {
        return hash(input.toByteArray())
    }
}