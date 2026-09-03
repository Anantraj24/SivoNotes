package com.anant.sivonotes.di

import android.content.Context
import com.anant.sivonotes.data.local.AppDatabase
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import com.anant.sivonotes.data.repository.NotesRepository
import com.anant.sivonotes.data.repository.RemindersRepository
import com.anant.sivonotes.data.repository.TodosRepository
import com.anant.sivonotes.data.repository.VaultRepository

class AppContainer(private val context: Context) {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val foldersRepository: FoldersRepository by lazy {
        FoldersRepository(database.folderDao())
    }

    val notesRepository: NotesRepository by lazy {
        NotesRepository(database.noteDao())
    }

    val importantPointsRepository: ImportantPointsRepository by lazy {
        ImportantPointsRepository(database.importantPointDao())
    }

    val todosRepository: TodosRepository by lazy {
        TodosRepository(database.todoDao())
    }

    val remindersRepository: RemindersRepository by lazy {
        RemindersRepository(database.reminderDao())
    }

    val vaultRepository: VaultRepository by lazy {
        VaultRepository(database.vaultDao(), context)
    }
}
