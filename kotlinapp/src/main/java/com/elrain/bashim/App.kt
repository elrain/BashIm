package com.elrain.bashim

import android.app.Application
import android.arch.persistence.room.Room

class App: Application() {

    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDatabase::class.java,"bashDatabase.db")
                .allowMainThreadQueries().build()
    }

    fun getAppDb() = db

}