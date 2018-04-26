package com.github.suhininalex.codefinder.preprocessing

import com.github.suhininalex.codefinder.preprocessing.tokens.TokenJsonSerializer
import com.github.suhininalex.codefinder.utils.findFilesByType
import java.io.File

class TokenJsonReader(private val file: String){

    val files: Sequence<FileDescription>
        get() {
            return File(file).findFilesByType("json").map { file ->
                file.reader().use { input ->
                    TokenJsonSerializer.fromJson(input, FileDescription::class.java)
                }
            }
        }
}