package com.planlectura.biblia90.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/** Un capítulo leído. El progreso se guarda por capítulo para poder cambiar de plan sin perderlo. */
@Entity(tableName = "chapter_read", primaryKeys = ["bookId", "chapter"])
data class ChapterReadEntity(
    val bookId: String,
    val chapter: Int,
    val readAt: Long,
)

@Dao
interface ChapterReadDao {
    @Query("SELECT * FROM chapter_read")
    fun observeAll(): Flow<List<ChapterReadEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ChapterReadEntity>)

    @Delete
    suspend fun deleteAll(items: List<ChapterReadEntity>)

    @Query("DELETE FROM chapter_read")
    suspend fun clear()
}

@Database(entities = [ChapterReadEntity::class], version = 1, exportSchema = true)
abstract class ReadingDatabase : RoomDatabase() {
    abstract fun chapterReadDao(): ChapterReadDao
}
