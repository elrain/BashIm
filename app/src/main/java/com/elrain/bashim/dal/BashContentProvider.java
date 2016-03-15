package com.elrain.bashim.dal;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.util.BashPreferences;

import javax.inject.Inject;

/**
 * Created by denys.husher on 03.11.2015.
 * Application content provider
 */
public class BashContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.elrain.bashim.Bash";
    private static final String QUOTES_PATH = "quotes";
    public static final Uri QUOTES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + QUOTES_PATH);

    private static final String QUOT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + QUOTES_PATH;
    private static final String QUOT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + QUOTES_PATH;

    private static final int URI_ALL_QUOTES = 1;
    private static final int URI_QUOT_ID = 2;

    private static final UriMatcher URI_MATCHER;

    @Inject BashPreferences mBashPreferences;
    @Inject DBHelper mDbHelper;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, QUOTES_PATH, URI_ALL_QUOTES);
        URI_MATCHER.addURI(AUTHORITY, QUOTES_PATH + "/#", URI_QUOT_ID);
    }

    @Override
    public boolean onCreate() {
        if (null != getContext()){
            ((BashApp)getContext()).getComponent().inject(this);
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case URI_ALL_QUOTES:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = QuotesTableHelper.PUB_DATE + " DESC ";
                break;
            case URI_QUOT_ID:
                break;
            default:
                throw new IllegalArgumentException("Not valid URI");
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(QuotesTableHelper.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        if (null != getContext() && null != getContext().getContentResolver())
            cursor.setNotificationUri(getContext().getContentResolver(), QUOTES_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case URI_ALL_QUOTES:
                return QUOT_CONTENT_TYPE;
            case URI_QUOT_ID:
                return QUOT_CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        try {
            checkUri(uri);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long rowId = db.insertOrThrow(QuotesTableHelper.TABLE, null, values);
            if (rowId != -1)
                if (null == values.getAsString(QuotesTableHelper.AUTHOR)
                        || "".equals(values.getAsString(QuotesTableHelper.AUTHOR)))
                    if (null != getContext())
                        mBashPreferences.increaseQuotCounter();
            Uri resultUri = ContentUris.withAppendedId(uri, rowId);
            if (null != getContext() && null != getContext().getContentResolver())
                getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        } catch (SQLiteException e) {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        checkUri(uri);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updatedRows = db.update(QuotesTableHelper.TABLE, values, selection, selectionArgs);
        if (null != getContext() && null != getContext().getContentResolver())
            getContext().getContentResolver().notifyChange(uri, null);
        return updatedRows;
    }

    private void checkUri(@NonNull Uri uri) {
        if (URI_MATCHER.match(uri) != URI_ALL_QUOTES)
            throw new IllegalArgumentException("Wrong URI: " + uri);
    }
}
