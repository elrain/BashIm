package com.elrain.bashim.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.elrain.bashim.util.BashPreferences;

import javax.inject.Inject;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "com_elrain_bashim.db";
    @Inject BashPreferences mBashPreferences;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        QuotesTableHelper.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 3:
                QuotesTableHelper.update3To4(db);
                mBashPreferences.resetFirstStart();
                break;
        }
    }
}
