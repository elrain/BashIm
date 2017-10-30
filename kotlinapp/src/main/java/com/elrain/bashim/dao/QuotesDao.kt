package com.elrain.bashim.dao

import android.arch.persistence.room.*
import com.elrain.bashim.BashItemType
import com.elrain.bashim.entities.BashItem
import com.elrain.bashim.entities.TABLE_NAME

@Dao
interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveNewQuotes(bashItem: List<BashItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveQuote(bashItem: BashItem): Long

    @Query("SELECT _id, link, title, description, pubDate " +
            "FROM $TABLE_NAME " +
            "WHERE author IS NULL " +
            "ORDER BY pubDate DESC")
    fun getQuotes(): List<BashItem>

    @Query("SELECT _id, link, title, description, pubDate, author " +
            "FROM $TABLE_NAME " +
            "WHERE author IS NOT NULL " +
            "ORDER BY pubDate DESC")
    fun getCommics(): List<BashItem>

    @Query("SELECT q._id, q.author, q.description, q.link, q.pubDate, q.title " +
            "FROM tempTable as t " +
            "LEFT JOIN $TABLE_NAME as q ON t.idQuote = q._id")
    fun getTempItems(): List<BashItem>

    @Query("DELETE FROM ${TABLE_NAME} " +
            "WHERE _id IN (SELECT t.idQuote FROM tempTable as t)")
    fun deleteOtherItems()

}