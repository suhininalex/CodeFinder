package com.github.suhininalex.codefinder.index.document

import com.github.suhininalex.codefinder.index.description.MethodIndex
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.CallToken
import com.github.suhininalex.codefinder.preprocessing.tokens.Token
import com.github.suhininalex.codefinder.search.api.Document
import com.github.suhininalex.codefinder.search.api.Section
import com.github.suhininalex.codefinder.string.StemTokenizer
import com.github.suhininalex.codefinder.utils.findPositions
import com.github.suhininalex.codefinder.utils.slice

private typealias Context = List<String>
typealias MethodId = String
typealias Word = String

class DocumentProducer(private val methodIndex: MethodIndex){

    private val tokenizer = StemTokenizer

    fun useDocuments(body: (Sequence<Document<MethodId, Word>>) -> Unit){
        methodIndex.useMethods { methods ->
            val documents = methods.map { method -> createDocument(method) }
            body(documents)
        }
    }

    private fun createDocument(method: MethodDescription): Document<MethodId, Word> {
        return Document(
                method.qualifiedName,
                listOf(
                        getJavaDocSection(method),
                        getDeclarationSection(method),
                        getContentSection(method),
                        getUsageContextSection(method)
                )
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

    private fun getUsageContextSection(method: MethodDescription): Section<Word> {
        return Section(
                section = "usage",
                content = findUsageContexts(method.qualifiedName).flatten().flatMap { tokenizer.tokenize(it) }
        )
    }

    private fun getContentSection(method: MethodDescription): Section<Word> {
        return Section(
                section = "content",
                content = method.content.flatMap { tokenizer.tokenize(it.name) }
        )
    }

    private fun getDeclarationSection(method: MethodDescription): Section<Word> {
        return Section(
                section = "declaration",
                content = tokenizer.tokenize(method.declaration)
        )
    }

    private fun getJavaDocSection(method: MethodDescription): Section<Word> {
        return Section(
                section = "javaDoc",
                content = tokenizer.tokenize(method.javaDoc)
        )
    }
}