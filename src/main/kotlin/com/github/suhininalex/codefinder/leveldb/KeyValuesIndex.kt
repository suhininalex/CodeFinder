package com.github.suhininalex.codefinder.leveldb

import org.iq80.leveldb.DB
import java.io.*

class KeyValuesIndex<Key, Value>(
        private val db: DB,
        private val tablePrefix: String,
        private val keyExternalizer: DataExternalizer<Key>,
        private val valueExternalizer: DataExternalizer<Value>) {

    fun get(key: Key): Set<Value> {
        val outputBytes = ByteArrayOutputStream()
        val output = DataOutputStream(outputBytes)
        output.writeUTF(tablePrefix)
        keyExternalizer.write(output, key)
        val keyBytes = outputBytes.toByteArray()

        db.iterator().use { it ->
            it.seek(keyBytes)
            return it.asSequence()
                    .map { entryFromBytes(it.key) }
                    .takeWhile { (entryKey, _) -> entryKey == key }
                    .map { (_, entryValue) -> entryValue }
                    .toSet()
        }
    }

    fun put(key: Key, value: Value){
        val entryBytes = entryToBytes(key, value)
        db.put(entryBytes, ByteArray(0))
    }

    fun putAll(entries: List<Entry<Key, Value>>){
        db.createWriteBatch().use { batch ->
            entries.forEach { (key, value) -> batch.put(entryToBytes(key, value), ByteArray(0)) }
            db.write(batch)
        }
    }

    fun contains(key: Key, value: Value): Boolean {
        val entryBytes = entryToBytes(key, value)
        return db.get(entryBytes) != null
    }

    fun remove(key: Key, value: Value){
        val entryBytes = entryToBytes(key, value)
        db.delete(entryBytes)
    }

    fun removeAll(entries: List<Entry<Key, Value>>){
        db.createWriteBatch().use { batch ->
            entries.forEach { (key, value) -> batch.delete(entryToBytes(key, value)) }
            db.write(batch)
        }
    }


    fun <R> useEntries(body: (Sequence<Entry<Key, Value>>) -> R): R {
        db.iterator().use { it ->
            it.seekToFirst()
            val entries =  it.asSequence().map { entryFromBytes(it.key) }
            return body(entries)
        }
    }

    private fun entryFromBytes(bytes: ByteArray): Entry<Key, Value> {
        val input = DataInputStream(ByteArrayInputStream(bytes))
        val tablePrefix = input.readUTF()
        val key = keyExternalizer.read(input)
        val value = valueExternalizer.read(input)
        return Entry(key, value)
    }

    private fun entryToBytes(key: Key, value: Value): ByteArray {
        val outputBytes = ByteArrayOutputStream()
        val output = DataOutputStream(outputBytes)
        output.writeUTF(tablePrefix)
        keyExternalizer.write(output, key)
        valueExternalizer.write(output, value)
        return outputBytes.toByteArray()
    }
}