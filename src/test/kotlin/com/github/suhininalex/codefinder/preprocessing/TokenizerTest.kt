package com.github.suhininalex.codefinder.preprocessing

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.suhininalex.codefinder.utils.normalize
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TokenizerTest {

    private val code =
            """ package com.test.pack;

                public class TestClass {
                    /**
                     *  JavaDoc sample
                     */
                    void method(Param p){
                        // Comment Example
                        Integer result = "literal".concat("another").hashCode();
                    }
                }
            """

    private val expectedDescription = MethodDescription(
            qualifiedName = "com.test.pack.TestClass.method(Param)",
            className = "TestClass",
            packageName = "com.test.pack",
            javaDoc = "JavaDoc sample",
            declaration = "void method(Param p)",
            content = listOf(
                    CommentToken(" Comment Example"),
                    TypeToken("Integer"), IdentifierToken("result"), LiteralToken("literal"),
                    CallToken("concat", "java.lang.String.concat(java.lang.String)"),
                    LiteralToken("another"), CallToken("hashCode", "java.lang.String.hashCode()")
            ),
            rawContent = normalize(
                    """
                    {
                        // Comment Example
                        Integer result = "literal".concat("another").hashCode();
                    }
                """
            )

    )

    @Test
    fun `description parse test`() {
        JavaParser.getStaticConfiguration().configureSolver()
        val method: MethodDeclaration = JavaParser.parse(code).findFirst(MethodDeclaration::class.java).get()
        val description: MethodDescription = JavaProcessor().parse(method)
        assertThat(description).isEqualTo(expectedDescription)
    }

    @Test
    fun `serialization test`(){
        val json: String = TokenJsonSerializer.toJson(expectedDescription)
        assertThat(TokenJsonSerializer.fromJson(json, MethodDescription::class.java)).isEqualTo(expectedDescription)
    }
}