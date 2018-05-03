package com.github.suhininalex.codefinder.index.description

import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.CallToken
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PersistedMethodIndexTest {

    private val methodIndex: MethodIndex
        get() {
            return PersistedMethodIndex("src/test/data/libgdx/index/method")
        }

    @Test
    fun `test libgdx index`() {
        val methodGetX = methodIndex.getMethodById("com.badlogic.gdx.Input.getX()")
        assertThat(methodGetX).isNotNull()
        assertThat(methodGetX?.content).isEmpty()
        assertThat(methodGetX?.className).isEqualTo("Input")
    }

    @Test
    fun `test libgdx usages`() {
        val methodName = "com.badlogic.gdx.Input.getX()"
        val usages = methodIndex.getUsagesOf(methodName).mapNotNull { methodIndex.getMethodById(it) }
        fun MethodDescription.containsCall(qualifiedName: String): Boolean {
            return content.filterIsInstance<CallToken>().any { it.reference == qualifiedName }
        }
        assertThat(usages).isNotEmpty
        usages.forEach {
            assertThat(it.containsCall(methodName)).isTrue()
        }
    }
}