package com.github.suhininalex.codefinder.preprocessing.tokens

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseProblemException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class JavaParserUtilsTest {

    private fun packageNameOf(code: String): String? {
        return JavaParser.parse(code).imports.first().packageName
    }

    @Test
    fun getPackageName() {
        assertThat(packageNameOf("import java.io.Class;")).isEqualTo("java.io")
        assertThat(packageNameOf("import static java.io.Class.*;")).isEqualTo("java.io")
        assertThat(packageNameOf("import java.io.*;")).isEqualTo("java.io")
        assertThat(packageNameOf("import static java.io.Class.method;")).isEqualTo("java.io")
        assertThat(packageNameOf("import static method;")).isNull()

        assertThatThrownBy { packageNameOf("i'm not parsable") }.isInstanceOf(ParseProblemException::class.java)
    }
}