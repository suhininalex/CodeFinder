package com.github.suhininalex.codefinder.search.impl

import com.github.suhininalex.codefinder.leveldb.Entry
import com.github.suhininalex.codefinder.search.api.SectionIndex
import org.assertj.core.api.Assertions.assertThat
import org.fusesource.leveldbjni.JniDBFactory
import org.fusesource.leveldbjni.JniDBFactory.factory
import org.iq80.leveldb.DB
import org.iq80.leveldb.Options
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class LevelDbSectionIndexTest {

    private val pathToDb = File("src/test/data/leveldb/")
    private val indexId = 0x01

    @Before
    fun setUp(){
        useSectionIndex { index ->
            entries.forEach { (id, words) -> index.indexSection(id, words) }
        }
    }

    @After
    fun cleanUp(){
        JniDBFactory.factory.destroy(pathToDb, Options())
    }

    private fun useSectionIndex(body: (SectionIndex<SectionID, WORD>) -> Unit){
        val db = factory.open(pathToDb, Options())
        val index = LevelDbSectionIndex(db, indexId)
        body(index)
        db.close()
    }

    private fun split(string: String) = string.split(" ")

    private val entries = listOf(
        Entry("qualified.name.1", split("test content words")),
        Entry("qualified.name.2", split("test other tokens"))
    )

    @Test
    fun `test find by word`() {
        useSectionIndex { index ->
            assertThat(index.findSectionsByWord("test")).containsOnly("qualified.name.1", "qualified.name.2")
            assertThat(index.findSectionsByWord("content")).containsOnly("qualified.name.1")
            assertThat(index.findSectionsByWord("outlier")).isEmpty()
        }
    }

    @Test
    fun `test delete entries`() {
        useSectionIndex { index ->
            assertThat(index.findSectionsByWord("words")).containsOnly("qualified.name.1")
            index.delete("qualified.name.1", listOf("words"))
            assertThat(index.findSectionsByWord("words")).isEmpty()
        }
        useSectionIndex { index ->
            assertThat(index.findSectionsByWord("words")).isEmpty()
        }
        `test find by word`()
    }

}