package com.elrain.bashim.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "com_elrain_bashim.db";
    private static DBHelper mInstance;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static DBHelper getInstance(@NonNull Context context) {
        if (null == mInstance)
            mInstance = new DBHelper(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        QuotesTableHelper.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2)
            QuotesTableHelper.from1To2(db);
        if (oldVersion == 2 && newVersion == 3)
            QuotesTableHelper.from2To3(db);
    }
}
