package com.github.suhininalex.codefinder.string

import com.github.suhininalex.codefinder.string.EnglishStemmer.stem
import org.assertj.core.api.Assertions
import org.junit.Test

class EnglishStemmerTest {

    @Test
    fun `basic test for stemmer`() {
        Assertions.assertThat(stem("positions")).isEqualTo(stem("positionally"))
    }
}