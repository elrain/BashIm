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

    private Activity mActivity;
    private LoaderManager.LoaderCallbacks<Cursor> mClazz;
    private int mLoaderId;

    public SearchHelper(Activity activity, LoaderManager.LoaderCallbacks<Cursor> clazz, int loaderId) {
        this.mActivity = activity;
        this.mClazz = clazz;
        this.mLoaderId = loaderId;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
            mActivity.getLoaderManager().restartLoader(mLoaderId, null, mClazz);
            return false;
        } else if (newText.length() < 3)
            return false;
        else {
            Bundle b = new Bundle();
            b.putString(Constants.KEY_SEARCH_STRING, newText);
            mActivity.getLoaderManager().restartLoader(mLoaderId, b, mClazz);
            return true;
        }
    }
}