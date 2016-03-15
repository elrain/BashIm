package com.elrain.bashim.fragment.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.SearchView;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.util.BashPreferences;

import javax.inject.Inject;

public class SearchHelper implements SearchView.OnQueryTextListener {

    @Inject BashPreferences mBashPreferences;

    public SearchHelper(@NonNull Context context) {
        ((BashApp)context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            mBashPreferences.setSearchFilter(query);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mBashPreferences.setSearchFilter(null);
            return true;
        }
        return false;
    }
}
