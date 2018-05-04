package com.github.suhininalex.codefinder.search.impl

import com.github.suhininalex.codefinder.search.api.Document
import com.github.suhininalex.codefinder.search.api.Section
import org.assertj.core.api.Assertions.assertThat
import org.fusesource.leveldbjni.JniDBFactory.factory
import org.iq80.leveldb.Options
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.io.File

class PersistentDocumentIndexTest {

    private val pathToDb = File("src/test/data/leveldb/")
    private val inverseWordsTable = 0x01
    private val documentsTable = 0x02

    @Before
    fun setUp(){
        useDocumentIndex { index ->
            entries.forEach { index.indexDocument(it) }
        }
    }

    @After
    fun cleanUp(){
        factory.destroy(pathToDb, Options())
    }

    private fun useDocumentIndex(body: (PersistentDocumentIndex) -> Unit){
        val db = factory.open(pathToDb, Options())
        val index = PersistentDocumentIndex(db, inverseWordsTable, documentsTable)
        body(index)
        db.close()
    }

    @Test
    fun `test if content is correctly merged`() {
        val document = Document(
            documentId = "qualified.name.1",
            sections = listOf(
                    Section("title", listOf("test", "title")),
                    Section("content", listOf("some", "random", "content")),
                    Section("empty", emptyList())
            )
        )
        useDocumentIndex { index ->
            assertThat(index.createCommonContent(document)).containsOnly("test", "title", "some", "random", "content")
        }
    }

    private val entries = listOf(
        Document(
                documentId = "qualified.name.1",
                sections = listOf(
                        Section("title", listOf("test", "title")),
                        Section("content", listOf("some", "random", "content")),
                        Section("empty", emptyList())
                )
        ),
        Document(
                documentId = "qualified.name.2",
                sections = listOf(
                        Section("title", listOf("another", "title")),
                        Section("content", listOf("completely", "new", "content"))
                )
        )
    )

    @Test
    fun `test document exists`() {
        useDocumentIndex { index ->
            val document = index.getDocumentById("qualified.name.1")
            assertThat(document?.documentId).isEqualTo("qualified.name.1")
        }
    }

    @Test
    fun `basic search test`() {
        useDocumentIndex { index ->
            assertThat(index.search("title")).containsOnly("qualified.name.1", "qualified.name.2")
            assertThat(index.search("new")).containsOnly("qualified.name.2")
            assertThat(index.search("outlier")).isEmpty()
        }
    }

    @Test
    fun `test document removal`() {
        useDocumentIndex { index ->
            index.deleteDocument("qualified.name.1")
        }
        useDocumentIndex { index ->
            assertThat(index.search("title")).containsOnly("qualified.name.2")
            assertThat(index.getDocumentById("qualified.name.1")).isNull()
        }
    }
}