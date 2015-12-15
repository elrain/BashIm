package com.elrain.bashim.fragment.helper;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SearchView;

import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 06.11.2015.
 */
public class SearchHelper implements SearchView.OnQueryTextListener {

    private final Activity mActivity;
    private final LoaderManager.LoaderCallbacks<Cursor> mClazz;

    public SearchHelper(Activity activity, LoaderManager.LoaderCallbacks<Cursor> clazz) {
        this.mActivity = activity;
        this.mClazz = clazz;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Bundle b = new Bundle();
        b.putString(Constants.KEY_SEARCH_STRING, query);
        mActivity.getLoaderManager().restartLoader(Constants.ID_LOADER, b, mClazz);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0)
            mActivity.getLoaderManager().restartLoader(Constants.ID_LOADER, null, mClazz);
        return false;
    }
}
