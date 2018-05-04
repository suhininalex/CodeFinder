package com.github.suhininalex.codefinder.search.api

interface InverseWordIndex<ID, in WORD> {
    fun index(id: ID, content: List<WORD>)
    fun delete(id: ID, content: List<WORD>)
    fun findSectionsByWord(word: WORD): Set<ID>
}