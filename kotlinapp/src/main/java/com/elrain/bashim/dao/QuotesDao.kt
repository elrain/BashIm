package com.elrain.bashim.dao

import android.arch.persistence.room.*
import com.elrain.bashim.entities.*

@Dao
interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveNewQuotes(bashItem: List<BashItem>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveQuote(bashItem: BashItem): Long

    @Query("SELECT $QUOTES_TABLE_ID, $QUOTES_LINK, $QUOTES_TITLE, $QUOTES_DESCRIPTION, " +
            "$QUOTES_PUB_DATE " +
            "FROM $QUOTES_TABLE_NAME " +
            "WHERE $QUOTES_AUTHOR IS NULL " +
            "ORDER BY $QUOTES_PUB_DATE DESC")
    fun getQuotes(): List<BashItem>

    @Query("SELECT $QUOTES_TABLE_ID, $QUOTES_LINK, $QUOTES_TITLE, $QUOTES_DESCRIPTION, " +
            "$QUOTES_PUB_DATE, $QUOTES_AUTHOR " +
            "FROM $QUOTES_TABLE_NAME " +
            "WHERE $QUOTES_AUTHOR IS NOT NULL " +
            "ORDER BY $QUOTES_PUB_DATE DESC")
    fun getCommics(): List<BashItem>

    @Query("SELECT q.$QUOTES_TABLE_ID, q.$QUOTES_AUTHOR, q.$QUOTES_DESCRIPTION, q.$QUOTES_LINK, " +
            "q.$QUOTES_PUB_DATE, q.$QUOTES_TITLE " +
            "FROM $TEMP_TABLE_NAME as t " +
            "LEFT JOIN $QUOTES_TABLE_NAME as q ON t.$TEMP_QUOTE_ID = q.$QUOTES_TABLE_ID")
    fun getTempItems(): List<BashItem>

    @Query("DELETE FROM $QUOTES_TABLE_NAME " +
            "WHERE $QUOTES_TABLE_ID IN (SELECT t.$TEMP_QUOTE_ID FROM $TEMP_TABLE_NAME as t)")
    fun deleteOtherItems()

}