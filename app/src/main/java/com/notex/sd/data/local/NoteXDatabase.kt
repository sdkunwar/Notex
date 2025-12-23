package com.notex.sd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notex.sd.data.local.dao.FolderDao
import com.notex.sd.data.local.dao.NoteDao
import com.notex.sd.data.local.dao.TagDao
import com.notex.sd.data.local.entity.FolderEntity
import com.notex.sd.data.local.entity.NoteEntity
import com.notex.sd.data.local.entity.NoteTagCrossRef
import com.notex.sd.data.local.entity.TagEntity

@Database(
    entities = [
        NoteEntity::class,
        FolderEntity::class,
        TagEntity::class,
        NoteTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NoteXDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
    abstract fun tagDao(): TagDao

    companion object {
        const val DATABASE_NAME = "notex_database"
    }
}
