package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.object.BashItem;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class QuotesTableHelper {
    public static final String TABLE = "quots";
    public static final String ID = "_id";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String PUB_DATE = "pubDate";
    public static final String DESCRIPTION = "description";
    public static final String IS_FAVORITE = "isFavorite";
    public static final String AUTHOR = "author";
    public static final String[] MAIN_SELECTION = {ID, DESCRIPTION, TITLE, PUB_DATE, LINK, IS_FAVORITE,
            AUTHOR};
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LINK + " TEXT NOT NULL, "
            + TITLE + " VARCHAR(50) NOT NULL, " + PUB_DATE + " DATE NOT NULL, "
            + DESCRIPTION + " TEXT NOT NULL, " + IS_FAVORITE + " BOOLEAN, " + AUTHOR + " CHAR(50), "
            + "UNIQUE(" + LINK + ") ON CONFLICT IGNORE)";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void saveQuot(Context context, BashItem bashItem) {
        ContentValues cv = new ContentValues();
        cv.put(LINK, bashItem.getLink());
        cv.put(TITLE, bashItem.getTitle());
        cv.put(PUB_DATE, bashItem.getPubDate().getTime());
        cv.put(DESCRIPTION, bashItem.getDescription());
        cv.put(IS_FAVORITE, false);
        cv.put(AUTHOR, bashItem.getAuthor());
        context.getContentResolver().insert(BashContentProvider.QUOTES_CONTENT_URI, cv);
    }

    public static String[] getTextToShare(Context context, long id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(
                            BashContentProvider.QUOTES_CONTENT_URI, "/" + id), new String[]{DESCRIPTION, LINK, AUTHOR},
                    ID + " = ?", new String[]{String.valueOf(id)}, null);
            if (null != cursor && cursor.moveToNext()) {
                return new String[]{cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(LINK)),
                        cursor.getString(cursor.getColumnIndex(AUTHOR))};
            }
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }

    public static String getUrlForComicsById(Context context, long id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(
                            BashContentProvider.QUOTES_CONTENT_URI, "/" + id), new String[]{DESCRIPTION}, ID + "=?",
                    new String[]{String.valueOf(id)}, null);
            if (null != cursor && cursor.moveToNext())
                return cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }

    public static void makeFavorite(Context context, long id, boolean isFavorite) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, isFavorite);
        context.getContentResolver().update(BashContentProvider.QUOTES_CONTENT_URI, cv, ID + "=?", new String[]{String.valueOf(id)});
    }
}
