package com.github.suhininalex.codefinder.index.description

import com.github.suhininalex.codefinder.leveldb.DataExternalizer
import com.github.suhininalex.codefinder.leveldb.StringExternalizer
import com.github.suhininalex.codefinder.leveldb.StringListExternalizer
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.Token
import com.github.suhininalex.codefinder.preprocessing.tokens.TokenJsonSerializer
import java.io.DataInput
import java.io.DataOutput

object MethodExternalizer: DataExternalizer<MethodDescription> {

    override fun read(dataInput: DataInput): MethodDescription {
        return MethodDescription(
                packageName = StringExternalizer.read(dataInput),
                className = StringExternalizer.read(dataInput),
                qualifiedName = StringExternalizer.read(dataInput),
                javaDoc = StringExternalizer.read(dataInput),
                declaration = StringExternalizer.read(dataInput),
                rawContent = StringExternalizer.read(dataInput),
                content = StringListExternalizer.read(dataInput).map { TokenJsonSerializer.fromJson(it, Token::class.java) }
        )
    }

    override fun write(dataOutput: DataOutput, data: MethodDescription) {
        StringExternalizer.write(dataOutput, data.packageName)
        StringExternalizer.write(dataOutput, data.className)
        StringExternalizer.write(dataOutput, data.qualifiedName)
        StringExternalizer.write(dataOutput, data.javaDoc)
        StringExternalizer.write(dataOutput, data.declaration)
        StringExternalizer.write(dataOutput, data.rawContent)
        StringListExternalizer.write(dataOutput, data.content.map { TokenJsonSerializer.toJson(it, Token::class.java) })
    }
}