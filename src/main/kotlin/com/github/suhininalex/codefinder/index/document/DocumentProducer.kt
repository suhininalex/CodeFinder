package com.github.suhininalex.codefinder.index.document

import com.github.suhininalex.codefinder.index.MethodIndex
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.CallToken
import com.github.suhininalex.codefinder.preprocessing.tokens.Token
import com.github.suhininalex.codefinder.string.StemTokenizer
import com.github.suhininalex.codefinder.utils.findPositions
import com.github.suhininalex.codefinder.utils.slice

private typealias Context = List<String>
typealias MethodId = String
typealias Word = String

class DocumentProducer(private val methodIndex: MethodIndex){

    fun createUsageContextSection(methodId: MethodId): Section<Word> {
        return Section<Word>(
            section = "usage",
            content = findUsageContexts(methodId).flatten().flatMap { StemTokenizer.tokenize(it) }
        )
    }

    internal fun MethodDescription.findContextFor(qualifiedName: String, contextSize: Int): List<List<Token>> {
        val indices = content.findPositions { it is CallToken && it.reference == qualifiedName }
        return indices.map { it-contextSize..it }.map { content.slice(it) }
    }

    private fun findUsageContexts(methodId: MethodId, contextSize: Int = 20): List<Context> {
        val usages = methodIndex.getUsagesOf(methodId).mapNotNull { methodIndex.getMethodById(it) }
        return usages.map { it.findContextFor(qualifiedName = methodId, contextSize = contextSize) }
                .flatten()
                .map { it.map { it.name } }
    }
}