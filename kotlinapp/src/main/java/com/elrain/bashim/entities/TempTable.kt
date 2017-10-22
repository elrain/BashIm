package com.elrain.bashim.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tempTable")
class TempTable {

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var ID: Long = 1

    @ColumnInfo(name = "idQuote")
    var ID_QUOTE: Int = 1
}