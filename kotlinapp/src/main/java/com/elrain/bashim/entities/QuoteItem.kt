package com.elrain.bashim.entities

import android.arch.persistence.room.*
import android.text.TextUtils
import java.util.*

const val TABLE_NAME = "quotes"

@Entity(tableName = TABLE_NAME,
        indices = arrayOf(Index(name = "link", value = "link", unique = true)))
data class BashItem(
        @ColumnInfo(name = "_id")
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,
        var title: String = "",
        var description: String = "",
        var link: String = "",
        var pubDate: Date = Date(),
        var author: String? = null) {

    @Ignore
    fun isQuote(): Boolean = TextUtils.isEmpty(author)
}