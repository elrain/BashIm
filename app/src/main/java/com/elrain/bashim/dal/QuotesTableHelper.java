package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.object.ImageSimpleItem;

import java.util.ArrayList;

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
    static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LINK + " TEXT NOT NULL, "
            + TITLE + " VARCHAR(50) NOT NULL, " + PUB_DATE + " DATE NOT NULL, "
            + DESCRIPTION + " TEXT NOT NULL, " + IS_FAVORITE + " BOOLEAN, " + AUTHOR + " CHAR(50), "
            + "UNIQUE(" + LINK + ") ON CONFLICT ABORT)";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void update3To4(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            long id = 1;
            cursor = db.rawQuery("SELECT " + DESCRIPTION + ", " + TITLE + ", " + PUB_DATE + ", "
                    + LINK + ", " + IS_FAVORITE + ", " + AUTHOR + " FROM " + QuotesTableHelper.TABLE, null);
            db.execSQL("DROP TABLE IF EXISTS " + QuotesTableHelper.TABLE);
            db.execSQL(CREATE_TABLE);
            while (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                cv.put(ID, id);
                cv.put(PUB_DATE, cursor.getLong(cursor.getColumnIndex(PUB_DATE)));
                cv.put(IS_FAVORITE, cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1);
                cv.put(LINK, cursor.getString(cursor.getColumnIndex(LINK)));
                cv.put(AUTHOR, cursor.getString(cursor.getColumnIndex(AUTHOR)));
                cv.put(DESCRIPTION, cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                cv.put(TITLE, cursor.getString(cursor.getColumnIndex(TITLE)));
                db.insert(TABLE, null, cv);
                ++id;
            }
        } finally {
            if (null != cursor) cursor.close();
        }
    }

    public static void saveQuot(Context context, BashItem bashItem) {
        ContentValues cv = new ContentValues();
        cv.put(LINK, bashItem.getLink());
        cv.put(TITLE, bashItem.getTitle());
        cv.put(PUB_DATE, bashItem.getPubDate().getTime());
        cv.put(DESCRIPTION, bashItem.getDescription());
        cv.put(IS_FAVORITE, bashItem.isFavorite());
        cv.put(AUTHOR, bashItem.getAuthor());
        context.getContentResolver().insert(BashContentProvider.QUOTES_CONTENT_URI, cv);
    }

    public static ArrayList<ImageSimpleItem> getImages(Context context) {
        Cursor cursor = null;
        ArrayList<ImageSimpleItem> images = new ArrayList<>();
        try {
            cursor = context.getContentResolver().query(BashContentProvider.QUOTES_CONTENT_URI,
                    new String[]{ID, DESCRIPTION, TITLE, PUB_DATE}, AUTHOR + " IS NOT NULL ", null, null);
            while (null != cursor && cursor.moveToNext()) {
                ImageSimpleItem isi = new ImageSimpleItem();
                isi.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                isi.setLink(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                isi.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                images.add(isi);
            }
        } finally {
            if (null != cursor) cursor.close();
        }
        return images;
    }

    public static void makeFavorite(Context context, long id, boolean isFavorite) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, isFavorite);
        context.getContentResolver().update(BashContentProvider.QUOTES_CONTENT_URI, cv, ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public static String getUrlForComicsById(Context context, long id) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.withAppendedPath(
                            BashContentProvider.QUOTES_CONTENT_URI, "/" + id), new String[]{DESCRIPTION},
                    AUTHOR + " IS NOT NULL AND " + ID + "=?",
                    new String[]{String.valueOf(id)}, null);
            if (null != cursor && cursor.moveToNext())
                return cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }

    public static void makeOrInsertAsFavorite(Context context, BashItem item) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, !isFavorite(context, item.getLink()));
        long updatedRow = context.getContentResolver().update(Uri.withAppendedPath(
                        BashContentProvider.QUOTES_CONTENT_URI, "/" + 0), cv, LINK + " =? ",
                new String[]{item.getLink()});
        if (updatedRow == 0) {
            item.setIsFavorite(true);
            saveQuot(context, item);
        }
    }

    public static boolean isFavorite(Context mContext, String link) {
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(BashContentProvider.QUOTES_CONTENT_URI,
                    new String[]{IS_FAVORITE}, LINK + " =? ", new String[]{link}, null);
            if (null != cursor && cursor.moveToNext())
                return cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1;
        } finally {
            if (null != cursor) cursor.close();
        }
        return false;
    }
}
