package com.elrain.bashim.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import com.elrain.bashim.entities.QuotesTable

@Dao
interface QuotesDao {

    @Insert
    fun saveNewQuotes(quotesTable: Array<QuotesTable>)


}