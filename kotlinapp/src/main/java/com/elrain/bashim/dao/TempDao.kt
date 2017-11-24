package com.elrain.bashim.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.elrain.bashim.entities.TempEntity

@Dao
interface TempDao {

    @Insert
    fun insertRef(tempEntity: TempEntity)

    @Query("DELETE FROM tempTable")
    fun deleteOldRows()

}