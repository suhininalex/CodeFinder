package com.github.suhininalex.codefinder.leveldb

import org.assertj.core.api.Assertions.assertThat
import org.fusesource.leveldbjni.JniDBFactory.factory
import org.iq80.leveldb.Options
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class KeyValueIndexTest {

    private val pathToDb = File("src/test/data/leveldb")
    private val tablePrefix = 0x01

    private fun useTestIndex(body:(KeyValueIndex<Int, String>)->Unit){
        val db = factory.open(pathToDb, Options())
        val index = KeyValueIndex(db, tablePrefix, IntExternalizer, StringExternalizer)
        body(index)
        db.close()
    }

    @Before
    fun setUp(){
        useTestIndex { index ->
            entriesDB.forEach { entry ->
                index.put(entry.key, entry.value)
            }
        }
    }

    @After
    fun cleanUp(){
        factory.destroy(pathToDb, Options())
    }

    private val entriesDB = listOf(
        Entry(3, "three"),
        Entry(4, "four")
    )

    @Test
    fun `test put get chain`() {
        useTestIndex { index ->
            assertThat(index.get(3)).isEqualTo("three")
            assertThat(index.contains(3)).isTrue()
            assertThat(index.get(0)).isNull()
            assertThat(index.contains(0)).isFalse()

            val extractedEntries = index.useEntries { it.toList() }
            assertThat(entriesDB).isEqualTo(extractedEntries)
        }
    }

    @Test
    fun `test remove`() {
        useTestIndex { index ->
            index.remove(3)
            assertThat(index.contains(4)).isTrue()
            assertThat(index.contains(3)).isFalse()
        }
    }
}