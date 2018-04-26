package com.github.suhininalex.codefinder.index

import com.github.suhininalex.codefinder.leveldb.createBytes
import com.github.suhininalex.codefinder.leveldb.useBytes
import com.github.suhininalex.codefinder.preprocessing.MethodDescription
import com.github.suhininalex.codefinder.preprocessing.tokens.*
import com.github.suhininalex.codefinder.utils.normalize
import org.junit.Test

class MethodExternalizerTest{
    private val givenMethod = MethodDescription(
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
    fun `test method externalizer`(){
        val byteArray = createBytes { dataOutput -> MethodExternalizer.write(dataOutput, givenMethod) }
        val extractedMethod = useBytes(byteArray) { dataInput -> MethodExternalizer.read(dataInput)}
    }
}