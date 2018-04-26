package com.github.suhininalex.codefinder.string

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringTokenizerTest {

    @Test
    fun `test WordTokenizer on typical method content`(){
        with(WordTokenizer){
            assertThat(tokenize("someMethodName2")).isEqualTo("some method id")
            assertThat(tokenize("capsMETHOD")).isEqualTo("caps method")
            assertThat(tokenize("Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);")).isEqualTo("gdx gl gl clear gl gl color buffer bit")
            assertThat(tokenize("if (debugUnderMouse || debugParentUnderMouse || debugTableUnderMouse != Debug.none) {"))
                    .isEqualTo("if debug under mouse debug parent under mouse debug table under mouse debug none")
        }
    }

    @Test
    fun `test WordTokenizer on typical javaDoc`(){
        val javadoc = """
            /** @return The x coordinate of the last touch on touch screen devices and the current mouse position on desktop for the first
             *         pointer in screen coordinates. The screen origin is the top left corner. */
            """
        val javadocTokens= "return the x coordinate of the last touch on touch screen devices and the current mouse position on desktop for the first pointer in screen coordinates the screen origin is the top left corner"
        assertThat(WordTokenizer.tokenize(javadoc)).isEqualTo(javadocTokens)
    }

    @Test
    fun `basic test for StemTokenizer`(){
        assertThat(StemTokenizer.tokenize("get mouse position")).containsExactly("get", "mous", "posit")
    }
}