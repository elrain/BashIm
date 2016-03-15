package com.elrain.bashim.module;

import android.app.Application;

import com.elrain.bashim.dal.DBHelper;
import com.elrain.bashim.util.AlarmUtil;
import com.elrain.bashim.util.BashPreferences;

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
    DBHelper getDbHelper(Application application) {
        return new DBHelper(application);
    }
}
