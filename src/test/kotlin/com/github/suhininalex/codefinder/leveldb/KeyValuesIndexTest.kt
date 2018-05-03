package com.github.suhininalex.codefinder.leveldb

import org.assertj.core.api.Assertions.assertThat
import org.fusesource.leveldbjni.JniDBFactory
import org.iq80.leveldb.Options
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class KeyValuesIndexTest {

    private val pathToDb = File("src/test/data/leveldb")
    private val tablePrefix = 0x01

    private fun useTestIndex(body:(KeyValuesIndex<Int, String>)->Unit){
        val db = JniDBFactory.factory.open(pathToDb, Options())
        val index = KeyValuesIndex(db, tablePrefix, IntExternalizer, StringExternalizer)
        body(index)
        db.close()
    }

    @Before
    fun setUp(){
        JniDBFactory.factory.destroy(pathToDb, Options())
        useTestIndex { index ->
            entriesDB.forEach { entry ->
                index.put(entry.key, entry.value)
            }
        }
    }

    private val entriesDB = listOf(
        Entry(1, "one"),
        Entry(1, "one's"),
        Entry(1, "ONE"),
        Entry(2, "two")
    )

    @After
    fun cleanUp(){
        JniDBFactory.factory.destroy(pathToDb, Options())
    }

    @Test
    fun `test get`(){
        useTestIndex { index ->
            assertThat(index.get(1)).containsOnly("one", "one's", "ONE")
            assertThat(index.get(2)).containsOnly("two")
            assertThat(index.get(0)).isEmpty()
        }
    }

    @Test
    fun `test removeAll`(){
        useTestIndex { index ->
            index.removeAll(
                listOf(
                    Entry(1, "one"),
                    Entry(1, "one's"),
                    Entry(1, "not exist"),
                    Entry(2, "two")
                )
            )
        }
        useTestIndex { index ->
            assertThat(index.get(1)).containsOnly("ONE")
            assertThat(index.get(2)).isEmpty()
        }
    }

    @Test
    fun `test putAll`(){
        cleanUp()
        useTestIndex { index ->
            index.putAll(entriesDB)
        }
        `test get`()
    }
}