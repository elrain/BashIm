package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.object.ImageSimpleItem;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    public static Observable<List<BashItem>> getBashItems(BriteDatabase db, String filter, int count) {
        Observable<SqlBrite.Query> queryObservable;
        if (TextUtils.isEmpty(filter)) {
            queryObservable = db.createQuery(TABLE,
                    String.format(Locale.US, "SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s IS NULL " +
                                    "ORDER BY %s DESC, ROWID LIMIT %d", ID, DESCRIPTION, TITLE, PUB_DATE,
                            LINK, IS_FAVORITE, AUTHOR, TABLE, AUTHOR, PUB_DATE, count));
        } else {
            queryObservable = db.createQuery(TABLE,
                    String.format(Locale.US, "SELECT %s, %s, %s, %s, %s, %s, %s FROM %s WHERE %s IS NULL " +
                                    "AND %s LIKE '%%%s%%' ORDER BY %s DESC, ROWID LIMIT %d",
                            ID, DESCRIPTION, TITLE, PUB_DATE, LINK, IS_FAVORITE, AUTHOR, TABLE,
                            AUTHOR, DESCRIPTION, filter, PUB_DATE, count));
        }
        return queryObservable.map(query -> {
            List<BashItem> items = new ArrayList<>();
            Cursor cursor = query.run();
            while (cursor.moveToNext()) {
                BashItem item = new BashItem();
                item.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                item.setAuthor(cursor.getString(cursor.getColumnIndex(AUTHOR)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                item.setLink(cursor.getString(cursor.getColumnIndex(LINK)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                item.setIsFavorite(cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1);
                item.setPubDate(new Date(cursor.getLong(cursor.getColumnIndex(PUB_DATE))));
                items.add(item);
            }
            return items;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public static void saveQuot(BriteDatabase db, BashItem bashItem) {
        ContentValues cv = new ContentValues();
        cv.put(LINK, bashItem.getLink());
        cv.put(TITLE, bashItem.getTitle());
        cv.put(PUB_DATE, bashItem.getPubDate().getTime());
        cv.put(DESCRIPTION, bashItem.getDescription());
        cv.put(IS_FAVORITE, bashItem.isFavorite());
        cv.put(AUTHOR, bashItem.getAuthor());
        db.insert(TABLE, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static List<ImageSimpleItem> getImages(BriteDatabase db) {
        Cursor cursor = null;
        ArrayList<ImageSimpleItem> images = new ArrayList<>();
        try {
            cursor = db.query(String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s IS NOT NULL ",
                    ID, DESCRIPTION, TITLE, PUB_DATE, TABLE, AUTHOR));
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

    public static void makeFavorite(BriteDatabase db, long id, boolean isFavorite) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, isFavorite);
        db.update(TABLE, cv, ID + "=?", String.valueOf(id));
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

    public static void makeOrInsertAsFavorite(BriteDatabase db, BashItem item) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, !isFavorite(db, item.getLink()));
        long updatedRow = db.update(TABLE, cv, LINK + " =? ", item.getLink());
        if (updatedRow == 0) {
            item.setIsFavorite(true);
            saveQuot(db, item);
        }
    }

    public static boolean isFavorite(BriteDatabase db, String link) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format("SELECT %s FROM %s WHERE %s =? ", IS_FAVORITE, TABLE,
                    LINK), link);
            if (null != cursor && cursor.moveToNext())
                return cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1;
        } finally {
            if (null != cursor) cursor.close();
        }
        return false;
    }

    @Nullable
    public static Date getLastQuotePubTime(BriteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format("SELECT %s from %s WHERE %s = (SELECT max(%s) FROM %s)",
                    PUB_DATE, TABLE, ID, ID, TABLE));
            if (cursor.moveToNext())
                return new Date(cursor.getLong(cursor.getColumnIndex(PUB_DATE)));
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }
}
