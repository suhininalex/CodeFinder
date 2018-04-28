package com.github.suhininalex.codefinder.leveldb

import org.iq80.leveldb.DB

class KeyValueIndex<Key, Value>(
    private val db: DB,
    private val tablePrefix: String,
    private val keyExternalizer: DataExternalizer<Key>,
    private val valueExternalizer: DataExternalizer<Value>){

    fun get(key: Key): Value? {
        val keyBytes = keyToBytes(key)
        val valueBytes = db.get(keyBytes) ?: return null
        return valueFromBytes(valueBytes)
    }

    fun put(key: Key, value: Value){
        val keyBytes = keyToBytes(key)
        val valueBytes = valueToBytes(value)
        db.put(keyBytes, valueBytes)
    }

    fun contains(key: Key): Boolean {
        return get(key) != null
    }

    fun <R> useEntries(body: (Sequence<Entry<Key, Value>>) -> R): R {
        db.iterator().use { it ->
            it.seekToFirst()
            val entries =  it.asSequence().filter { it.key.isNotEmpty() && it.value.isNotEmpty()}
                    .map { (keyBytes, valueBytes) ->
                        Entry(keyFromBytes(keyBytes), valueFromBytes(valueBytes))
                    }
            return body(entries)
        }
    }

    fun remove(key: Key){
        db.delete(keyToBytes(key))
    }

    private fun keyToBytes(key: Key): ByteArray {
        return createBytes { out ->
            out.writeUTF(tablePrefix)
            keyExternalizer.write(out, key)
        }
    }

    private fun valueToBytes(value: Value): ByteArray {
        return createBytes { out -> valueExternalizer.write(out, value) }
    }

    private fun valueFromBytes(bytes: ByteArray): Value {
        return useBytes(bytes) { input -> valueExternalizer.read(input) }
    }

    private fun keyFromBytes(bytes: ByteArray): Key {
        return useBytes(bytes) { input ->
            val tablePrefix = input.readUTF()
            keyExternalizer.read(input)
        }
    }
}

