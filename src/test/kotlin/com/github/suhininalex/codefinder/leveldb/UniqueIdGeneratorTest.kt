package com.github.suhininalex.codefinder.leveldb

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.Test

class UniqueIdGeneratorTest {

    @Test
    fun `test if same input gives same id`() {
        val input = "test".toByteArray()
        assertThat(IdGenerator().idFrom(input)).isEqualTo(IdGenerator().idFrom(input))
    }

    @Test
    fun `duplicated inputs throw error`(){
        val generator = IdGenerator()
        val input = "test".toByteArray()
        assertThatCode {
            generator.idFrom(input)
            generator.idFrom(input)
        }
        .hasMessageContaining("already exists")
    }
}