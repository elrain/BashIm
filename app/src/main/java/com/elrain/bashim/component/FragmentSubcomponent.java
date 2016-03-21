package com.elrain.bashim.component;

import com.elrain.bashim.fragment.BestRandomFragment;
import com.elrain.bashim.fragment.ComicsFragment;
import com.elrain.bashim.fragment.FavoriteFragment;
import com.elrain.bashim.fragment.MainFragment;

import dagger.Subcomponent;

@Subcomponent
public interface FragmentSubcomponent {
    void inject(MainFragment fragment);

    void inject(BestRandomFragment fragment);

    void inject(FavoriteFragment fragment);

    void inject(ComicsFragment fragment);
}
