package com.elrain.bashim.entities

import android.arch.persistence.room.*
import android.text.TextUtils
import java.util.*

@Entity(tableName = QUOTES_TABLE_NAME,
        indices = arrayOf(Index(name = QUOTES_LINK, value = QUOTES_LINK, unique = true)))
data class BashItem(
        @ColumnInfo(name = QUOTES_TABLE_ID)
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