package com.elrain.bashim

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.elrain.bashim.converters.DateConverter
import com.elrain.bashim.dao.QuotesDao
import com.elrain.bashim.dao.TempDao
import com.elrain.bashim.entities.BashItem
import com.elrain.bashim.entities.TempEntity

@Database(entities = arrayOf(BashItem::class, TempEntity::class), version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quotesDao(): QuotesDao
    abstract fun tempDao(): TempDao
}