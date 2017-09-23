package com.elrain.bashim.dal

import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import com.elrain.bashim.dal.helpers.BashItemType
import com.elrain.bashim.dal.helpers.QuotesTableHelper

class ItemsLoader(context: Context, dbHelper: DBHelper) : CursorLoader(context) {

    private val mDbHelper: DBHelper = dbHelper

    override fun loadInBackground(): Cursor {
        val type = BashItemType.getTypeById(id)
        return if (type == BashItemType.OTHER) {
            QuotesTableHelper.getOtherItems(mDbHelper.readableDatabase) as Cursor
        } else {
            QuotesTableHelper.getQuotesOrCommics(type, mDbHelper.readableDatabase)
        }
    }

}
