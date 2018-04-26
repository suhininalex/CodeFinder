package com.github.suhininalex.codefinder.index

import com.github.suhininalex.codefinder.leveldb.KeyValueIndex
import com.github.suhininalex.codefinder.leveldb.KeyValuesIndex
import com.github.suhininalex.codefinder.leveldb.StringExternalizer
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.CallToken
import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import java.io.File

class PersistedMethodIndex(path: String): MethodIndex {
    private val db: DB = JniDBFactory.factory.open(File(path), Options())

    private val methods: KeyValueIndex<String, MethodDescription> = KeyValueIndex(
            db = db,
            tablePrefix = "#methods",
            keyExternalizer = StringExternalizer,
            valueExternalizer = MethodExternalizer
    )

    private val usages: KeyValuesIndex<String, String> = KeyValuesIndex(
            db = db,
            tablePrefix = "#usages",
            keyExternalizer = StringExternalizer,
            valueExternalizer = StringExternalizer
    )

    override fun indexMethod(method: MethodDescription){
        methods.put(method.qualifiedName, method)
        method.content.filterIsInstance<CallToken>().forEach { token ->
            usages.put(token.reference, method.qualifiedName)
        }
    }

    override fun getMethodById(id: String): MethodDescription? {
        return methods.get(id)
    }

    override fun getUsagesOf(id: String): Set<String> {
        return usages.get(id)
    }
}