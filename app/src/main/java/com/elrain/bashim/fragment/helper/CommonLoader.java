package com.elrain.bashim.fragment.helper;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.dal.QuotesTableHelper;

/**
 * Created by denys.husher on 16.11.2015.
 */
public class CommonLoader {

    private String mWhereClause;
    private String[] mWhereValues;
    private Context mContext;

    public CommonLoader(Context mContext) {
        this.mContext = mContext;
    }

    public CommonLoader getComics(){
        mWhereClause = QuotesTableHelper.AUTHOR + " IS NOT NULL";
        mWhereValues = null;
        return this;
    }

    public CommonLoader getQuotes(){
        mWhereClause = QuotesTableHelper.AUTHOR + " IS NULL";
        return this;
    }

    public CommonLoader getFavorites(){
        mWhereClause = QuotesTableHelper.IS_FAVORITE + " =? ";
        mWhereValues = new String[]{String.valueOf(1)};
        return this;
    }

    public CommonLoader addSearch(String text){
        mWhereClause += " AND " + QuotesTableHelper.DESCRIPTION + " LIKE '%" + text + "%'";
        return this;
    }

    public Loader<Cursor> build(){
        return new CursorLoader(mContext, BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, mWhereClause, mWhereValues, null);
    }
}
