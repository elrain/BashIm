package com.elrain.bashim.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface TempDao {

    @Query("INSERT INTO tempTable(idQuote) VALUES(:refId)")
    fun insertRef(refId: Long)

    @Query("DELETE FROM tempTable")
    fun deleteOldRows()

}