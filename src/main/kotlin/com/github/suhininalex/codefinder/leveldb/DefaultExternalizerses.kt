package com.github.suhininalex.codefinder.leveldb

import java.io.DataInput
import java.io.DataOutput

object IntExternalizer: DataExternalizer<Int> {
    override fun read(dataInput: DataInput): Int {
        return dataInput.readInt()
    }

    override fun write(dataOutput: DataOutput, data: Int) {
        dataOutput.writeInt(data)
    }
}

object StringExternalizer: DataExternalizer<String>{
    override fun read(dataInput: DataInput): String {
        return dataInput.readUTF()
    }

    override fun write(dataOutput: DataOutput, data: String) {
        dataOutput.writeUTF(data)
    }
}

object StringListExternalizer: DataExternalizer<List<String>> {
    override fun read(dataInput: DataInput): List<String> {
        val size = dataInput.readInt()
        return (1..size).map { dataInput.readUTF() }
    }

    override fun write(dataOutput: DataOutput, data: List<String>) {
        dataOutput.writeInt(data.size)
        data.forEach { dataOutput.writeUTF(it) }
    }
}