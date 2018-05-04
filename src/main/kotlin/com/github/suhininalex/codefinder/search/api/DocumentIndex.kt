package com.github.suhininalex.codefinder.search.api

interface DocumentIndex<ID, WORD> {
    fun <R> useDocuments(body: (Sequence<Document<ID, WORD>>) -> R): R
    fun indexDocument(document: Document<ID, WORD>)
    fun deleteDocument(documentId: ID)
    fun getDocumentById(id: ID): Document<ID, WORD>?
    fun search(word: WORD): Set<ID>
    fun searchByAllWords(words: Set<WORD>): Set<ID>
    fun searchByAnyWord(words: Set<WORD>): Set<ID>
}