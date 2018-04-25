package com.github.suhininalex.codefinder.preprocessing

private const val empty: String = ""

data class FileDescription(
        val packageName: String = empty,
        val imports: List<String> = emptyList(),
        val methods: List<MethodDescription> = emptyList()
)

data class MethodDescription(
        val qualifiedName: String = empty,
        val className: String = empty,
        val packageName: String = empty,
        val declaration: String = empty,
        val javaDoc: String = empty,
        val content: List<Token> = emptyList(),
        val rawContent: String = empty
)