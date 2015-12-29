package com.elrain.bashim.fragment.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.SearchView;

import com.elrain.bashim.util.BashPreferences;

public class SearchHelper implements SearchView.OnQueryTextListener {

    private final Context mContext;

    public SearchHelper(@NonNull Context context) {
        this.mContext = context;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            BashPreferences.getInstance(mContext).setSearchFilter(query);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            BashPreferences.getInstance(mContext).setSearchFilter(null);
            return true;
        }
        return false;
    }
}
