package com.elrain.bashim.module;

import android.app.Application;

import com.elrain.bashim.dal.DBHelper;
import com.elrain.bashim.util.AlarmUtil;
import com.elrain.bashim.util.BashPreferences;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ProviderModule {

    @Provides
    @Singleton
    BashPreferences getAppPreferences(Application application) {
        return BashPreferences.getInstance(application);
    }

    @Provides
    @Singleton
    AlarmUtil getAlarmUtil(Application application) {
        return AlarmUtil.getInstance(application);
    }

    @Provides
    @Singleton
    BriteDatabase getDbHelper(Application application) {
        return SqlBrite.create().wrapDatabaseHelper(new DBHelper(application));
    }
}
