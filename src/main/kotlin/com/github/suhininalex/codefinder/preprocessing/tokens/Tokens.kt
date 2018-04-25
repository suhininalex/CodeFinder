package com.github.suhininalex.codefinder.preprocessing.tokens

interface Token {
    val name: String
}

data class TypeToken(override val name: String = ""): Token

data class IdentifierToken(override val name: String = ""): Token

data class CallToken(override val name: String = "", val reference: String = ""): Token

data class LiteralToken(override val name: String = ""): Token

data class CommentToken(override val name: String = ""): Token



