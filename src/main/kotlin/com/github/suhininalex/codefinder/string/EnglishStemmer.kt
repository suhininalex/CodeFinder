package com.github.suhininalex.codefinder.string

import org.tartarus.snowball.ext.englishStemmer
import java.util.concurrent.ConcurrentHashMap

object EnglishStemmer {

    private val cache = ConcurrentHashMap<String, String>()
    private val stemmer = englishStemmer()

    private fun englishStemmer.stem(word: String): String {
        kotlin.synchronized(this@EnglishStemmer) {
            current = word
            stem()
            return current
        }
    }

    fun stem(word: String): String {
        return cache.computeIfAbsent(word) {
            stemmer.stem(word)
        }
    }
}