package com.elrain.bashim.component;

import com.elrain.bashim.activity.MainActivity;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.BashContentProvider;
import com.elrain.bashim.fragment.FavoriteFragment;
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

    void inject(BashService service);

    void inject(CommonAdapter adapter);

    void inject(BashContentProvider provider);

    void inject(FavoriteFragment fragment);

    void inject(SearchHelper helper);
}
