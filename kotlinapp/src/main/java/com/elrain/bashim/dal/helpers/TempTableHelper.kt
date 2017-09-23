package com.elrain.bashim.dal.helpers

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class TempTableHelper {

    companion object {
        val TABLE_NAME = "tempTable"
        private val ID = "_id"
        val ID_QUOTE = "idQuote"
        val ALIAS = "t"

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
            QuotesTableHelper.deleteOtherItems(db)
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        }
    }
}