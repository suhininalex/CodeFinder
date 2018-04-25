package com.github.suhininalex.codefinder.preprocessing

import java.io.File

class TokenJsonReader(private val file: String){

    val files: Sequence<FileDescription>
        get() {
            File(file).reader().use { input ->
                val result = TokenJsonSerializer.fromJson(input, FileDescription::class.java)
                println(result)
            }
            return emptySequence()
        }
}

fun main(args: Array<String>) {
    val reader = TokenJsonReader("src/test/data/libgdx/description.json")
    reader.files
}