package com.elrain.bashim.dao

import android.arch.persistence.room.*
import com.elrain.bashim.entities.*

@Dao
interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveNewQuotes(bashItem: List<BashItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveQuote(bashItem: BashItem): Long

    @Query("SELECT ${EntitiesConstants.QUOTES_TABLE_ID}, ${EntitiesConstants.QUOTES_LINK}, " +
            "${EntitiesConstants.QUOTES_TITLE}, ${EntitiesConstants.QUOTES_DESCRIPTION}, " +
            "${EntitiesConstants.QUOTES_PUB_DATE} " +
            "FROM ${EntitiesConstants.QUOTES_TABLE_NAME} " +
            "WHERE ${EntitiesConstants.QUOTES_AUTHOR} IS NULL " +
            "ORDER BY ${EntitiesConstants.QUOTES_PUB_DATE} DESC")
    fun getQuotes(): List<BashItem>

    @Query("SELECT ${EntitiesConstants.QUOTES_TABLE_ID}, ${EntitiesConstants.QUOTES_LINK}, " +
            "${EntitiesConstants.QUOTES_TITLE}, ${EntitiesConstants.QUOTES_DESCRIPTION}, " +
            "${EntitiesConstants.QUOTES_PUB_DATE}, ${EntitiesConstants.QUOTES_AUTHOR} " +
            "FROM ${EntitiesConstants.QUOTES_TABLE_NAME} " +
            "WHERE ${EntitiesConstants.QUOTES_AUTHOR} IS NOT NULL " +
            "ORDER BY ${EntitiesConstants.QUOTES_PUB_DATE} DESC")
    fun getCommics(): List<BashItem>

    @Query("SELECT q.${EntitiesConstants.QUOTES_TABLE_ID}, q.${EntitiesConstants.QUOTES_AUTHOR}, " +
            "q.${EntitiesConstants.QUOTES_DESCRIPTION}, q.${EntitiesConstants.QUOTES_LINK}, " +
            "q.${EntitiesConstants.QUOTES_PUB_DATE}, q.${EntitiesConstants.QUOTES_TITLE} " +
            "FROM ${EntitiesConstants.TEMP_TABLE_NAME} as t " +
            "LEFT JOIN ${EntitiesConstants.QUOTES_TABLE_NAME} as q " +
            "ON t.${EntitiesConstants.TEMP_QUOTE_ID} = q.${EntitiesConstants.QUOTES_TABLE_ID}")
    fun getTempItems(): List<BashItem>

    @Query("DELETE FROM ${EntitiesConstants.QUOTES_TABLE_NAME} " +
            "WHERE ${EntitiesConstants.QUOTES_TABLE_ID} " +
            "IN (SELECT t.${EntitiesConstants.TEMP_QUOTE_ID} " +
            "       FROM ${EntitiesConstants.TEMP_TABLE_NAME} as t)")
    fun deleteOtherItems()

}