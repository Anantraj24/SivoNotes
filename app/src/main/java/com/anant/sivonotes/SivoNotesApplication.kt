package com.anant.sivonotes

import android.app.Application
import com.anant.sivonotes.di.AppContainer

class SivoNotesApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
