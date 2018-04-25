package com.github.suhininalex.codefinder.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CommonUtilsKtTest {

    @Test
    fun sliceTest() {
        val list = (0..100).toList()
        assertThat(list.slice(3..5)).isEqualTo(listOf(3, 4, 5))
        assertThat(list.slice(98..102)).isEqualTo(listOf(98, 99, 100))
        assertThat(list.slice(-2..2)).isEqualTo(listOf(0, 1, 2))
        assertThat(emptyList<Int>().slice(2..3)).isEmpty()
    }

    @Test
    fun clipTest(){
        assertThat(-2..12 clip 0..10).isEqualTo(0..10)
        assertThat(5..12 clip 0..10).isEqualTo(5..10)
        assertThat(-2..5 clip 0..10).isEqualTo(0..5)
        assertThat(3..6 clip 0..10).isEqualTo(3..6)
    }

    @Test
    fun expandTest(){
        assertThat(3..7 expand 3).isEqualTo(0..10)
    }

    @Test
    fun intersectionTest(){
        val sets = listOf(
                setOf(1, 2, 3, 4),
                setOf(2, 3, 5),
                setOf(1, 2, 3)
        )
        assertThat(sets.intersection()).isEqualTo(setOf(2, 3))
    }
}