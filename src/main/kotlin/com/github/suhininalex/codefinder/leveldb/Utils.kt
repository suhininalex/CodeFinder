package com.github.suhininalex.codefinder.leveldb

import java.io.*

interface DataExternalizer<T> {
    fun read(dataInput: DataInput): T
    fun write(dataOutput: DataOutput, data: T)
}

data class Entry<Key, Value>(val key: Key, val value: Value)

inline fun createBytes(generator: (DataOutput) -> Unit): ByteArray {
    val byteStream = ByteArrayOutputStream()
    val dataStream = DataOutputStream(byteStream)
    generator(dataStream)
    return byteStream.toByteArray()
}

inline fun <T> useBytes(bytes: ByteArray, reader: (DataInput) -> T): T {
    val inputStream = DataInputStream(ByteArrayInputStream(bytes))
    return reader(inputStream)
}