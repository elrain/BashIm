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
    public static final String GUID = "guid";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String PUB_DATE = "pubDate";
    public static final String DESCRIPTION = "description";
    public static final String IS_FAVORITE = "isFavorite";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GUID + " CHAR(65) NOT NULL, "
            + LINK + " TEXT NOT NULL, " + TITLE + " VARCHAR(50) NOT NULL, "
            + PUB_DATE + " DATE NOT NULL, " + DESCRIPTION + " TEXT NOT NULL, "
            + IS_FAVORITE + " BOOLEAN, " +
            "UNIQUE(" + GUID + ") ON CONFLICT IGNORE)";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void from1To2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + IS_FAVORITE + " BOOLEAN ");
    }

    public static void inputQuot(Context context, BashItem bashItem) {
        ContentValues cv = new ContentValues();
        cv.put(GUID, bashItem.getGuid());
        cv.put(LINK, bashItem.getLink());
        cv.put(TITLE, bashItem.getTitle());
        cv.put(PUB_DATE, bashItem.getPubDate().getTime());
        cv.put(DESCRIPTION, bashItem.getDescription());
        cv.put(IS_FAVORITE, false);
        context.getContentResolver().insert(BashContentProvider.QUOTS_CONTENT_URI, cv);
    }

    public static String getText(Context context, long id) {
        Cursor cursor = null;
        String result = null;
        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(
                            BashContentProvider.QUOTS_CONTENT_URI, "/" + id), new String[]{DESCRIPTION},
                    ID + " = ?", new String[]{String.valueOf(id)}, null);
            if (null != cursor && cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
                cursor.close();
            }
        } finally {
            if (null != cursor) cursor.close();
        }
        return result;
    }

    public static void makeFavorite(Context context, long id, boolean isFavorite) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, isFavorite);
        context.getContentResolver().update(BashContentProvider.QUOTS_CONTENT_URI, cv, ID + "=?", new String[]{String.valueOf(id)});
    }
}
