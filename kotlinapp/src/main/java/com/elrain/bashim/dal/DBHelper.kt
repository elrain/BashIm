package com.elrain.bashim.dal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.elrain.bashim.dal.helpers.QuotesTableHelper
import com.elrain.bashim.dal.helpers.TempTableHelper

private val DB_NAME = "bashIm.db"
private val DB_VERSION = 1

class DBHelper private constructor(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private var sInstance: DBHelper? = null

        fun getInstance(context: Context): DBHelper {
            if (sInstance == null) {
                sInstance = DBHelper(context)
            }

            return sInstance as DBHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        QuotesTableHelper.createTable(db)
        TempTableHelper.createTempTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}