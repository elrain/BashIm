package com.elrain.bashim.component;

import android.app.Fragment;

import com.elrain.bashim.activity.ImagePagerActivity;
import com.elrain.bashim.activity.MainActivity;
import com.elrain.bashim.adapter.CommonAdapterOld;
import com.elrain.bashim.dal.BashContentProvider;
import com.elrain.bashim.fragment.BestRandomFragment;
import com.elrain.bashim.fragment.FavoriteFragment;
import com.elrain.bashim.fragment.MainFragment;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.module.AppModule;
import com.elrain.bashim.module.ProviderModule;
import com.elrain.bashim.service.BashService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ProviderModule.class})
public interface SingletonComponent {
    void inject(MainActivity activity);
    void inject(ImagePagerActivity activity);

    void inject(BashService service);

    void inject(CommonAdapterOld adapter);

    void inject(BashContentProvider provider);

    void inject(MainFragment fragment);
    void inject(BestRandomFragment fragment);
    void inject(FavoriteFragment fragment);

    void inject(SearchHelper helper);
}
