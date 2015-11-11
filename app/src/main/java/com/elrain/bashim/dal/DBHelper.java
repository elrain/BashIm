package com.elrain.bashim.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "com_elrain_bashim.db";
    private static DBHelper mInstance;

    public synchronized static DBHelper getInstance(Context context){
        if(null == mInstance)
            mInstance = new DBHelper(context);
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        QuotesTableHelper.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2)
            QuotesTableHelper.from1To2(db);
    }
}
