package com.github.suhininalex.codefinder.string

interface StringTokenizer {
    fun tokenize(string: String): List<String>
}

object WordTokenizer: StringTokenizer {
    override fun tokenize(string: String): List<String> {
        return Regex("[A-Z]*[a-z]*")
                .findAll(string)
                .map { it.value.toLowerCase() }
                .filter { it.length > 1 }
                .toList()
    }
}

object StemTokenizer: StringTokenizer {
    override fun tokenize(string: String): List<String> {
        return WordTokenizer.tokenize(string).map { EnglishStemmer.stem(it) }.filter { it.length > 1 }
    }
}