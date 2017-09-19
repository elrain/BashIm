package com.elrain.bashim.dal.helpers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class TempTableHelper {

    companion object {
        private val TABLE_NAME = "tempTable"
        private val ID = "_id"
        private val ID_QUOTE = "idQuote"

        private val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME( " +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ID_QUOTE INTEGER ) "

        fun createTempTable(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_TABLE)
        }

        fun insertRef(db: SQLiteDatabase?, refId: Long) {
            val cv = ContentValues()
            cv.put(ID_QUOTE, refId)
            db?.insert(TABLE_NAME, null, cv)
        }

        fun deleteTableAndRefs(db: SQLiteDatabase?) {
            //TODO implement deleting table and refs
        }
    }
}