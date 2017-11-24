package com.elrain.bashim

import android.app.Application
import android.arch.persistence.room.Room

class App: Application() {

    private lateinit var db: AppDatabase
    private val backgroundRequestHandler by lazy { BackgroundRequestHandler() }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java,"bashDatabase.db").build()
    }

    fun doInBackground() = backgroundRequestHandler

    fun getAppDb() = db

}