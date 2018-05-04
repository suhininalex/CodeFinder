package com.github.suhininalex.codefinder.search.impl

import com.github.suhininalex.codefinder.leveldb.*
import com.github.suhininalex.codefinder.search.api.SectionIndex
import org.iq80.leveldb.DB

typealias SectionID = String
typealias WORD = String

class LevelDbSectionIndex(db: DB, indexId: Int): SectionIndex<SectionID, WORD> {

    private val index: KeyValuesIndex<WORD, SectionID> = KeyValuesIndex(
            db = db,
            tablePrefix = indexId,
            keyExternalizer = StringExternalizer,
            valueExternalizer = StringExternalizer
    )

    private fun entries(id: SectionID, content: List<WORD>): List<Entry<WORD, SectionID>> {
        return content.map { word -> Entry(word, id) }
    }

    override fun indexSection(id: SectionID, content: List<WORD>) {
        index.putAll(entries(id, content))
    }

    override fun delete(id: SectionID, content: List<WORD>) {
        index.removeAll(entries(id, content))
    }

    override fun findSectionsByWord(word: WORD): Set<SectionID> {
        return index.get(word)
    }

}