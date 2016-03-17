package com.elrain.bashim.fragment.helper;

import android.text.TextUtils;
import android.widget.SearchView;

import com.elrain.bashim.util.BashPreferences;

public class SearchHelper implements SearchView.OnQueryTextListener {

    private BashPreferences mBashPreferences;

    public SearchHelper(BashPreferences bashPreferences) {
        mBashPreferences = bashPreferences;
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
