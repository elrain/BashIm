package com.elrain.bashim.dal.helpers

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.elrain.bashim.dao.BashItem


class QuotesTableHelper {
    companion object {
        private val TABLE_NAME = "quotes"
        val ID = "_id"
        val LINK = "link"
        val TITLE = "title"
        val PUB_DATE = "pubDate"
        val DESCRIPTION = "description"
        val AUTHOR = "author"
        private val TRIGGER_NAME = "ignore_old"
        private val ALIAS = "q"

        private val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME( " +
                "$ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$LINK TEXT NOT NULL, " +
                "$TITLE VARCHAR(50) NOT NULL, " +
                "$PUB_DATE DATE NOT NULL, " +
                "$DESCRIPTION TEXT NOT NULL, " +
                "$AUTHOR CHAR(50), " +
                "UNIQUE($LINK) ON CONFLICT ABORT)"

        private val CREATE_TRIGGER = "CREATE TRIGGER IF NOT EXISTS $TRIGGER_NAME " +
                "BEFORE INSERT ON $TABLE_NAME " +
                "WHEN NEW.$PUB_DATE BETWEEN (SELECT datetime(0000000000)) " +
                "   AND (SELECT MAX($PUB_DATE) FROM $TABLE_NAME) " +
                "BEGIN " +
                "   SELECT RAISE(IGNORE);" +
                "END"

        fun createTable(db: SQLiteDatabase?) {
            db?.execSQL(CREATE_TABLE)
            db?.execSQL(CREATE_TRIGGER)
        }

        fun saveNewQuotes(db: SQLiteDatabase?, bashItems: List<BashItem>) {
            bashItems.sortedBy { it.pubDate }.forEach {
                saveQuote(db, it)
            }
        }

        fun saveTempQuotes(db: SQLiteDatabase?, bashItems: List<BashItem>) {
            TempTableHelper.createTempTable(db)
            bashItems.forEach {
                val insertedId = saveQuote(db, it) as Long
                TempTableHelper.insertRef(db, insertedId)
            }
        }

        private fun saveQuote(db: SQLiteDatabase?, bashItem: BashItem): Long? {
            val cv = ContentValues()
            cv.put(LINK, bashItem.link)
            cv.put(TITLE, bashItem.title)
            cv.put(PUB_DATE, bashItem.pubDate.time)
            cv.put(DESCRIPTION, bashItem.description)
            cv.put(AUTHOR, bashItem.author)
            return db?.insert(TABLE_NAME, null, cv)
        }

        fun getQuotesOrCommics(type: BashItemType, db: SQLiteDatabase?): Cursor {
            val c: Cursor?

            var whereCause = " $AUTHOR IS NULL "
            if (type == BashItemType.COMICS) {
                whereCause = " $AUTHOR IS NOT NULL "
            }

            try {
                c = db?.rawQuery("SELECT $ID, $LINK, $TITLE, $DESCRIPTION, $PUB_DATE, $AUTHOR" +
                        " FROM $TABLE_NAME WHERE $whereCause ORDER BY $PUB_DATE DESC ", null)
            } finally {
            }

            return c!!
        }

        fun getOtherItems(db: SQLiteDatabase?): Cursor {
            val c: Cursor?

            try {
                c = db?.rawQuery("SELECT $ALIAS.$ID, $ALIAS.$AUTHOR, $ALIAS.$DESCRIPTION, " +
                        "$ALIAS.$LINK, $ALIAS.$PUB_DATE, $ALIAS.$TITLE " +
                        "FROM ${TempTableHelper.TABLE_NAME} as ${TempTableHelper.ALIAS} " +
                        "LEFT JOIN $TABLE_NAME as $ALIAS " +
                        "ON ${TempTableHelper.ALIAS}.${TempTableHelper.ID_QUOTE} = $ALIAS.$ID", null)
            } finally {
            }

            return c!!
        }

        fun deleteOtherItems(db: SQLiteDatabase?){
            db?.delete(TABLE_NAME, " $ID IN " +
                    "(SELECT ${TempTableHelper.ID_QUOTE} FROM ${TempTableHelper.TABLE_NAME})", null)
        }
    }
}

