package com.github.suhininalex.codefinder.index.document

data class Document<out ID, out WORD>(val documentId: ID, val sections: List<Section<WORD>>)

data class Section<out WORD>(val section: String, val content: List<WORD>)