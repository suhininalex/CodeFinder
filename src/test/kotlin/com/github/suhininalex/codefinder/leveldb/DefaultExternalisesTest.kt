package com.github.suhininalex.codefinder.leveldb

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DefaultExternalisesTest {

    private fun <T> DataExternalizer<T>.testChain(input: T){
        val byteArray = createBytes { dataOut -> this.write(dataOut, input) }
        val inputAfterChain = useBytes(byteArray) { dataInput -> this.read(dataInput)}
        assertThat(input).isEqualTo(inputAfterChain)
    }

    private val longString = (1..1000).joinToString { "blank" }

    @Test
    fun `test IntExternalizer`(){
        with(IntExternalizer){
            testChain(42)
            testChain(-42)
            testChain(0)
        }
    }

    @Test
    fun `test StringExternaizer`(){
        with(StringExternalizer){
            testChain("input")
            testChain("")
            testChain(longString)
        }

    }

    @Test
    fun `test StringListExternalizer`(){
        with(StringListExternalizer){
            testChain(emptyList())
            testChain(listOf("one"))
            testChain(listOf("one","two","three"))
            testChain((1..10000).map { it.toString() })
        }

    }
}