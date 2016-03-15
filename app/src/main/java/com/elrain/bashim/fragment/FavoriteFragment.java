package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImageScaleActivity;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.BashContentProvider;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;

import javax.inject.Inject;

public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private CommonAdapter mQuotesCursorAdapter;
    @Inject BashPreferences mBashPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        ((BashApp)getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuotesCursorAdapter = new CommonAdapter(getActivity(), true);
        RecyclerView lvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        lvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvItems.setAdapter(mQuotesCursorAdapter);
        getLoaderManager().initLoader(Constants.ID_LOADER, null, FavoriteFragment.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBashPreferences.setFilterListener(
                () -> getLoaderManager().restartLoader(Constants.ID_LOADER, null, FavoriteFragment.this));
    }

    @Override
    public void onStop() {
        super.onStop();
        mBashPreferences.removeFilterListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.aRefresh).setVisible(false);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchHelper(getActivity()));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String filter = BashPreferences.getInstance(getActivity().getApplicationContext()).getSearchFilter();
        if (TextUtils.isEmpty(filter))
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.IS_FAVORITE + " =? ",
                    new String[]{String.valueOf(1)}, null);
        else return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.IS_FAVORITE + " =? "
                + " AND " + QuotesTableHelper.DESCRIPTION + " LIKE '%" + filter + "%'",
                new String[]{String.valueOf(1)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuotesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuotesCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = QuotesTableHelper.getUrlForComicsById(getActivity(), id);
        if (null != url) {
            Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
            intent.putExtra(Constants.KEY_INTENT_IMAGE_URL, url);
            getActivity().startActivity(intent);
        }
    }
}
