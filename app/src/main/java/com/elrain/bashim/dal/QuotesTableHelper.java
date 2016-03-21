package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.object.ImageSimpleItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.QueryBuilder;
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
    public static final String ALIAS = "q";
    static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LINK + " TEXT NOT NULL, "
            + TITLE + " VARCHAR(50) NOT NULL, " + PUB_DATE + " DATE NOT NULL, "
            + DESCRIPTION + " TEXT NOT NULL, " + IS_FAVORITE + " BOOLEAN, " + AUTHOR + " CHAR(50), "
            + "UNIQUE(" + LINK + ") ON CONFLICT ABORT)";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static Observable<List<BashItem>> getBashItems(Constants.QueryFilter queryFilter,
                                                          BriteDatabase db, String filter, int count) {
        Observable<SqlBrite.Query> queryObservable;
        queryObservable = db.createQuery(TABLE, QueryBuilder.getQueryString(queryFilter, filter, count));
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

    public static long saveQuote(BriteDatabase db, BashItem bashItem) {
        ContentValues cv = new ContentValues();
        cv.put(LINK, bashItem.getLink());
        cv.put(TITLE, bashItem.getTitle());
        cv.put(PUB_DATE, bashItem.getPubDate().getTime());
        cv.put(DESCRIPTION, bashItem.getDescription());
        cv.put(IS_FAVORITE, bashItem.isFavorite());
        cv.put(AUTHOR, bashItem.getAuthor());
        return db.insert(TABLE, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static List<ImageSimpleItem> getImages(BriteDatabase db) {
        Cursor cursor = null;
        ArrayList<ImageSimpleItem> images = new ArrayList<>();
        try {
            cursor = db.query(String.format("SELECT %s, %s, %s, %s FROM %s WHERE %s IS NOT NULL " +
                    "ORDER BY %s DESC", ID, DESCRIPTION, TITLE, PUB_DATE, TABLE, AUTHOR, PUB_DATE));
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

    public static ImageSimpleItem getImage(BriteDatabase db, long id) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format(Locale.US, "SELECT %s, %s, %s, %s FROM %s WHERE %s IS NOT NULL " +
                    "AND %s = %d", ID, DESCRIPTION, TITLE, PUB_DATE, TABLE, AUTHOR, ID, id));
            if (cursor.moveToNext()) {
                ImageSimpleItem isi = new ImageSimpleItem();
                isi.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                isi.setLink(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                isi.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                return isi;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void makeFavorite(BriteDatabase db, long id, boolean isFavorite) {
        ContentValues cv = new ContentValues();
        cv.put(IS_FAVORITE, isFavorite);
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            db.update(TABLE, cv, ID + "=?", String.valueOf(id));
            FavoriteInfoHelper.insertOrDeleteFavorite(db, id, isFavorite);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    @Nullable
    public static String getUrlForComicsById(BriteDatabase db, long id) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format(Locale.US, "SELECT %s FROM %s WHERE %s IS NOT NULL AND %s =?",
                    DESCRIPTION, TABLE, AUTHOR, ID), String.valueOf(id));
            if (null != cursor && cursor.moveToNext())
                return cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }

    public static void makeOrInsertAsFavorite(BriteDatabase db, BashItem item) {
        if (item.isFavorite())
            makeFavorite(db, item.getId(), !item.isFavorite());
        else {
            long id = getQuoteIdByLink(db, item.getLink());
            if (id == -1) {
                long newQuoteId = saveQuote(db, item);
                makeFavorite(db, newQuoteId, true);
            } else makeFavorite(db, id, true);
        }
    }

    public static long getQuoteIdByLink(BriteDatabase db, String link) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format("SELECT %s FROM %s WHERE %s =? ", ID, TABLE,
                    LINK), link);
            if (null != cursor && cursor.moveToNext())
                return cursor.getLong(cursor.getColumnIndex(ID));
        } finally {
            if (null != cursor) cursor.close();
        }
        return -1;
    }

    @Nullable
    public static Date getLastQuotePubTime(BriteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format("SELECT %s from %s ORDER BY %s DESC, ROWID LIMIT 1",
                    PUB_DATE, TABLE, PUB_DATE));
            if (cursor.moveToNext())
                return new Date(cursor.getLong(cursor.getColumnIndex(PUB_DATE)));
        } finally {
            if (null != cursor) cursor.close();
        }
        return null;
    }

    public static boolean isFavorite(BriteDatabase db, long id) {
        Cursor cursor = null;
        try {
            cursor = db.query(String.format("SELECT %s FROM %s WHERE %s = ? ", IS_FAVORITE, TABLE, ID), String.valueOf(id));
            if (cursor.moveToNext())
                return cursor.getInt(cursor.getColumnIndex(IS_FAVORITE)) == 1;
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }
}
