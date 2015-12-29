package com.elrain.bashim.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.elrain.bashim.util.BashPreferences;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "com_elrain_bashim.db";
    private static DBHelper mInstance;
    private final Context mContext;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
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
        switch (oldVersion) {
            case 3:
                QuotesTableHelper.update3To4(db);
                BashPreferences.getInstance(mContext).resetFirstStart();
                break;
        }
    }
}
