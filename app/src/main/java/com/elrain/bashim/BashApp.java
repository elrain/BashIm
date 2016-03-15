package com.elrain.bashim;

import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;

import com.elrain.bashim.component.DaggerSingletonComponent;
import com.elrain.bashim.component.SingletonComponent;
import com.elrain.bashim.module.AppModule;
import com.elrain.bashim.module.ProviderModule;

/**
 * Hint: mComponent was created in {@link Application#attachBaseContext(Context)}
 * because {@link ContentProvider#onCreate()} had been called before {@link Application#onCreate()}
 */
public class BashApp extends Application {

    private SingletonComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mComponent = DaggerSingletonComponent.builder().appModule(new AppModule(this))
                .providerModule(new ProviderModule()).build();
    }

    public SingletonComponent getComponent() {
        return mComponent;
    }

}
