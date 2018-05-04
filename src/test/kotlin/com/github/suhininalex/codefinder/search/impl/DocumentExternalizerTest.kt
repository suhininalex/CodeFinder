package com.github.suhininalex.codefinder.search.impl

import com.github.suhininalex.codefinder.leveldb.createBytes
import com.github.suhininalex.codefinder.leveldb.useBytes
import com.github.suhininalex.codefinder.search.api.Document
import com.github.suhininalex.codefinder.search.api.Section
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DocumentExternalizerTest {

    @Test
    fun `simple document externalizer test`() {
        val document = Document(
                documentId = "test.qualified.name.1",
                sections = listOf(
                    Section("title", listOf("test", "title")),
                    Section("content", listOf("some", "random", "content")),
                    Section("empty", emptyList())
                )
        )
        val bytes = createBytes { out -> DocumentExternalizer.write(out, document) }
        val serializedDocument = useBytes(bytes) { input -> DocumentExternalizer.read(input)}
        assertThat(document).isEqualTo(serializedDocument)
    }
}