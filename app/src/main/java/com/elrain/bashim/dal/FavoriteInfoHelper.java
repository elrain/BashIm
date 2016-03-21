package com.elrain.bashim.dal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;

/**
 * Created by denys.husher on 17.03.2016.
 */
public class FavoriteInfoHelper {

    public static final String TABLE = "favoriteInfo";
    public static final String ID = "_id";
    public static final String QUOTE_ID = "quoteId";
    public static final String ADDED_DATE = "addedDate";
    public static final String ALIAS = "fi";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + QUOTE_ID + " INTEGER REFERENCES "
            + QuotesTableHelper.TABLE + " (" + QuotesTableHelper.ID + ") ON DELETE CASCADE NOT NULL UNIQUE, "
            + ADDED_DATE + " DATETIME DEFAULT (datetime()));";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void insertOrDeleteFavorite(BriteDatabase db, long quoteId, boolean isFavorite) {
        if (isFavorite) FavoriteInfoHelper.insertFavorite(db, quoteId);
        else FavoriteInfoHelper.deleteFavorite(db, quoteId);

    }

    private static void insertFavorite(BriteDatabase db, long quoteId) {
        ContentValues cv = new ContentValues();
        cv.put(QUOTE_ID, quoteId);
        db.insert(TABLE, cv);
    }

    private static void deleteFavorite(BriteDatabase db, long quoteId) {
        db.delete(TABLE, QUOTE_ID + "=?", String.valueOf(quoteId));
    }
}
