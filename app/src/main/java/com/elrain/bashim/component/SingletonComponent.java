package com.elrain.bashim.component;

import com.elrain.bashim.activity.ImagePagerActivity;
import com.elrain.bashim.activity.ImageScaleActivity;
import com.elrain.bashim.activity.MainActivity;
import com.elrain.bashim.fragment.BestRandomFragment;
import com.elrain.bashim.fragment.ComicsFragment;
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

    FragmentSubcomponent plus();

    void inject(ImagePagerActivity activity);

    void inject(ImageScaleActivity activity);

    void inject(BashService service);

    void inject(SearchHelper helper);
}
