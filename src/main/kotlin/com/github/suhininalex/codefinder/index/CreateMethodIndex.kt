package com.github.suhininalex.codefinder.index

import com.github.suhininalex.codefinder.preprocessing.TokenJsonReader
import com.github.suhininalex.codefinder.utils.wrapWithProgressBar

fun main(args: Array<String>) {
    require(args.size == 2) { "Input json directory or output index directory is not defined"}
    val (inputJsonDirectory, indexDirectory) = args
    val methodIndex: MethodIndex = PersistedMethodIndex(indexDirectory)
    TokenJsonReader(inputJsonDirectory).files.toList()
            .wrapWithProgressBar("Building index from json files")
            .flatMap { it.methods.asSequence() }
            .forEach { methodIndex.indexMethod(it) }
}