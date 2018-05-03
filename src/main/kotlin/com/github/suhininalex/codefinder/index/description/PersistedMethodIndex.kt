package com.github.suhininalex.codefinder.index.description

import com.github.suhininalex.codefinder.leveldb.*
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.CallToken
import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import java.io.File

class PersistedMethodIndex(path: String): MethodIndex {

    private val db: DB = JniDBFactory.factory.open(
            File(path),
            Options().logger(LevelDbLogger)
    )

    private val methods: KeyValueIndex<String, MethodDescription> = KeyValueIndex(
            db = db,
            tablePrefix = GlobalGenerator.idFrom("#methods"),
            keyExternalizer = StringExternalizer,
            valueExternalizer = MethodExternalizer
    )

    private val usages: KeyValuesIndex<String, String> = KeyValuesIndex(
            db = db,
            tablePrefix = GlobalGenerator.idFrom("#methods"),
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

    override fun useMethods(body: (Sequence<MethodDescription>) -> Unit) {
        return methods.useEntries {
            body(it.map { it.value })
        }
    }
}