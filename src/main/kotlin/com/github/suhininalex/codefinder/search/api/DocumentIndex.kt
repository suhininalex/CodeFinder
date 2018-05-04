package com.github.suhininalex.codefinder.search.api

interface DocumentIndex<ID, WORD> {

    fun <R> useDocuments(body: (Sequence<Document<ID, WORD>>) -> R): R

    fun indexDocument(document: Document<ID, WORD>)

    fun deleteDocument(documentId: ID)

    fun getDocumentById(id: ID): Document<ID, WORD>?

    fun search(word: WORD): Set<ID>

    fun update(document: Document<ID, WORD>){
        deleteDocument(document.documentId)
        indexDocument(document)
    }
}