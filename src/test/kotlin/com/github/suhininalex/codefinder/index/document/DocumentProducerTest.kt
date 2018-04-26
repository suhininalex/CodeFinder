package com.github.suhininalex.codefinder.index.document

import com.github.suhininalex.codefinder.index.MethodIndex
import com.github.suhininalex.codefinder.index.PersistedMethodIndex
import com.github.suhininalex.codefinder.string.WordTokenizer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DocumentProducerTest {

    private val pathToIndex = "src/test/data/libgdx/index/method"

    @Test
    fun createUsageContextSection() {
        val methodIndex: MethodIndex = PersistedMethodIndex(pathToIndex)
        val methodName = "com.badlogic.gdx.Input.getX()"
        val contextMethod = methodIndex.getMethodById("com.badlogic.gdx.tests.GroupTest.render()")!!

        with(DocumentProducer(methodIndex)) {
            val tokenContexts = contextMethod.findContextFor(methodName, 20)
            val wordContexts = tokenContexts.map { it.flatMap { WordTokenizer.tokenize(it.name) } }
            val (firstContext, secondContext) = wordContexts

            val firstExpectedContext ="horiz set visible horiz set width gdx input get"
            assertThat(firstContext).isEqualTo(firstExpectedContext.split(" "))

            val secondExpectedContext ="get horiz set width horiz set height horiz fill horiz expand horiz invalidate horiz wrap set visible horiz wrap fill horiz wrap expand horiz wrap set width gdx input get"
            assertThat(secondContext).isEqualTo(secondExpectedContext.split(" "))
        }

    }
}