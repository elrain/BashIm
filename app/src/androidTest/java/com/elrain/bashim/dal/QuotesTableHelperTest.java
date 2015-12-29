package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.Date;

public class QuotesTableHelperTest extends InstrumentationTestCase {

    private SQLiteDatabase db;
    private DBHelperMemory mDbHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mDbHelper = new DBHelperMemory(getInstrumentation().getContext());
        db = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(QuotesTableHelper.TITLE, "title 1");
        cv.put(QuotesTableHelper.PUB_DATE, new Date().getTime());
        cv.put(QuotesTableHelper.LINK, "link 1");
        cv.put(QuotesTableHelper.AUTHOR, "author 1");
        cv.put(QuotesTableHelper.DESCRIPTION, "description 1");

        db.insert(QuotesTableHelper.TABLE, null, cv);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDbHelper = null;
    }

    @SmallTest
    public void testInsertSameItem() {
        ContentValues cv = new ContentValues();

        cv.put(QuotesTableHelper.TITLE, "title 1");
        cv.put(QuotesTableHelper.PUB_DATE, new Date().getTime());
        cv.put(QuotesTableHelper.LINK, "link 1");
        cv.put(QuotesTableHelper.AUTHOR, "author 1");
        cv.put(QuotesTableHelper.DESCRIPTION, "description 1");

        db.insert(QuotesTableHelper.TABLE, null, cv);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS c FROM " + QuotesTableHelper.TABLE, null);
            if (cursor.moveToNext())
                assertEquals(1, cursor.getInt(cursor.getColumnIndex("c")));
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    @SmallTest
    public void testInsertTwoItemsWithAbort() {
        ContentValues cv = new ContentValues();

        cv.put(QuotesTableHelper.TITLE, "title 1");
        cv.put(QuotesTableHelper.PUB_DATE, new Date().getTime());
        cv.put(QuotesTableHelper.LINK, "link 1");
        cv.put(QuotesTableHelper.AUTHOR, "author 1");
        cv.put(QuotesTableHelper.DESCRIPTION, "description 1");

        db.insert(QuotesTableHelper.TABLE, null, cv);

        cv.put(QuotesTableHelper.TITLE, "title 2");
        cv.put(QuotesTableHelper.PUB_DATE, new Date().getTime());
        cv.put(QuotesTableHelper.LINK, "link 2");
        cv.put(QuotesTableHelper.AUTHOR, "author 2");
        cv.put(QuotesTableHelper.DESCRIPTION, "description 2");

        db.insert(QuotesTableHelper.TABLE, null, cv);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(" + QuotesTableHelper.ID + ") AS m FROM " + QuotesTableHelper.TABLE, null);
            if (cursor.moveToNext()) assertEquals(2, cursor.getLong(cursor.getColumnIndex("m")));
            else fail();
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    private class DBHelperMemory extends SQLiteOpenHelper {

        public DBHelperMemory(Context context) {
            super(context, null, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(QuotesTableHelper.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + QuotesTableHelper.TABLE);
        }
    }
}
