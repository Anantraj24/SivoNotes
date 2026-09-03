package com.anant.sivonotes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anant.sivonotes.data.local.dao.FolderDao
import com.anant.sivonotes.data.local.dao.ImportantPointDao
import com.anant.sivonotes.data.local.dao.NoteDao
import com.anant.sivonotes.data.local.dao.ReminderDao
import com.anant.sivonotes.data.local.dao.TodoDao
import com.anant.sivonotes.data.local.dao.VaultDao
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.PrivateNoteEntity
import com.anant.sivonotes.data.local.entity.ReminderEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity

@Database(
    entities = [
        FolderEntity::class,
        NoteEntity::class,
        ImportantPointEntity::class,
        TodoEntity::class,
        ReminderEntity::class,
        VaultEntryEntity::class,
        PrivateNoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun noteDao(): NoteDao
    abstract fun importantPointDao(): ImportantPointDao
    abstract fun todoDao(): TodoDao
    abstract fun reminderDao(): ReminderDao
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sivo_notes_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
