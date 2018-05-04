package com.github.suhininalex.codefinder.search.api

data class Section<out WORD>(val section: String, val content: List<WORD>)