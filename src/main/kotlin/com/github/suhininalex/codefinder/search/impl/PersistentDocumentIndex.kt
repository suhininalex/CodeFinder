package com.github.suhininalex.codefinder.search.impl

import com.github.suhininalex.codefinder.leveldb.*
import com.github.suhininalex.codefinder.search.api.Document
import com.github.suhininalex.codefinder.search.api.DocumentIndex
import com.github.suhininalex.codefinder.search.api.InverseWordIndex
import com.github.suhininalex.codefinder.search.api.Section
import org.iq80.leveldb.DB
import java.io.DataInput
import java.io.DataOutput

class PersistentDocumentIndex(
        db: DB,
        inverseWordIndexTable: Int = GlobalGenerator.idFrom("#document_inverse_word_index"),
        documentIndexTable: Int = GlobalGenerator.idFrom("#document_content_index")
    ): DocumentIndex<DocumentID, WORD> {

    private val inverseWordIndex: InverseWordIndex<DocumentID, WORD> = PersistentInverseWordIndex(
        db = db,
        indexId = inverseWordIndexTable
    )

    private val documentIndex: KeyValueIndex<DocumentID, Document<DocumentID, WORD>> = KeyValueIndex(
        db = db,
        keyExternalizer = StringExternalizer,
        valueExternalizer = DocumentExternalizer,
        tablePrefix = documentIndexTable
    )

    internal fun createCommonContent(document: Document<DocumentID, WORD>): List<WORD> {
        return document.sections.flatMap { it.content }
    }

    override fun <R> useDocuments(body: (Sequence<Document<DocumentID, WORD>>) -> R): R {
        return documentIndex.useEntries { entries ->
            body(entries.map { it.value })
        }
    }

    override fun indexDocument(document: Document<DocumentID, WORD>) {
        val content = createCommonContent(document)
        inverseWordIndex.index(document.documentId, content)
        documentIndex.put(document.documentId, document)
    }

    override fun deleteDocument(documentId: DocumentID) {
        val document = getDocumentById(documentId) ?: return
        val content = createCommonContent(document)
        inverseWordIndex.delete(document.documentId, content)
        documentIndex.remove(document.documentId)
    }

    override fun getDocumentById(id: DocumentID): Document<DocumentID, WORD>? {
        return documentIndex.get(id)
    }

    override fun search(word: WORD): Set<DocumentID> {
        return inverseWordIndex.findSectionsByWord(word)
    }

}

object DocumentExternalizer: DataExternalizer<Document<DocumentID, WORD>> {
    override fun read(dataInput: DataInput): Document<DocumentID, WORD> {
        val documentId = dataInput.readUTF()
        val sectionsCount = dataInput.readInt()
        val sections = (1..sectionsCount).map {
            val name = dataInput.readUTF()
            val content = StringListExternalizer.read(dataInput)
            Section(name, content)
        }
        return Document(documentId, sections)
    }

    override fun write(dataOutput: DataOutput, data: Document<DocumentID, WORD>) {
        dataOutput.writeUTF(data.documentId)
        dataOutput.writeInt(data.sections.size)
        data.sections.forEach { section ->
            dataOutput.writeUTF(section.section)
            StringListExternalizer.write(dataOutput, section.content)
        }
    }

}