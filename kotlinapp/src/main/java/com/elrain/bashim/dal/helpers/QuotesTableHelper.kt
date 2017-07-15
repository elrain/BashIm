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

        fun saveQuote(db: SQLiteDatabase?, bashItem: BashItem): Long? {
            val cv: ContentValues = ContentValues()
            cv.put(LINK, bashItem.link)
            cv.put(TITLE, bashItem.title)
            cv.put(PUB_DATE, bashItem.pubDate.time)
            cv.put(DESCRIPTION, bashItem.description)
            cv.put(AUTHOR, bashItem.author)
            return db?.insert(TABLE_NAME, null, cv)
        }

        fun getItemsByType(type: BashItemType, db: SQLiteDatabase?): Cursor {
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
    }
}

enum class BashItemType(id: Int) {
    QUOTE(1), COMICS(2);

    private val mId: Int = id

    fun getId(): Int {
        return mId
    }

    companion object {
        fun getTypeById(id: Int): BashItemType {
            var retval : BashItemType = QUOTE
            when(id){
                2 -> retval = COMICS
            }
            return retval
        }
    }
}