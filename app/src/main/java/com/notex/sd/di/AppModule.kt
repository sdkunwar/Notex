package com.notex.sd.di

import android.content.Context
import androidx.room.Room
import com.notex.sd.data.local.NoteXDatabase
import com.notex.sd.data.local.dao.FolderDao
import com.notex.sd.data.local.dao.NoteDao
import com.notex.sd.data.local.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NoteXDatabase {
        return Room.databaseBuilder(
            context,
            NoteXDatabase::class.java,
            NoteXDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: NoteXDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideFolderDao(database: NoteXDatabase): FolderDao {
        return database.folderDao()
    }

    @Provides
    @Singleton
    fun provideTagDao(database: NoteXDatabase): TagDao {
        return database.tagDao()
    }
}
