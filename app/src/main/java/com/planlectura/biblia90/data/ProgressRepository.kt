package com.planlectura.biblia90.data

import com.planlectura.biblia90.domain.ChapterRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgressRepository(private val dao: ChapterReadDao) {

    val readChapters: Flow<Set<ChapterRef>> =
        dao.observeAll().map { list -> list.mapTo(HashSet()) { ChapterRef(it.bookId, it.chapter) } }

    suspend fun markRead(chapters: Collection<ChapterRef>) {
        val now = System.currentTimeMillis()
        dao.insertAll(chapters.map { ChapterReadEntity(it.bookId, it.chapter, now) })
    }

    suspend fun markUnread(chapters: Collection<ChapterRef>) {
        // @Delete compara sólo por clave primaria (bookId, chapter).
        dao.deleteAll(chapters.map { ChapterReadEntity(it.bookId, it.chapter, 0L) })
    }

    suspend fun reset() = dao.clear()
}
