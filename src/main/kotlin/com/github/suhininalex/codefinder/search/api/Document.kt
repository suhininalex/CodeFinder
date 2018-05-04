package com.github.suhininalex.codefinder.search.api

data class Document<out ID, out WORD>(val documentId: ID, val sections: List<Section<WORD>>)