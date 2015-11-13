package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.adapter.CommonCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.PostQuotListener;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 05.11.2015.
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CommonCursorAdapter mQuotesCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuotesCursorAdapter = new CommonCursorAdapter(getActivity(), null);
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnItemLongClickListener(new PostQuotListener(getActivity()));
        getLoaderManager().initLoader(Constants.ID_LOADER, null, FavoriteFragment.this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchHelper(getActivity(), this, Constants.ID_LOADER));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == args)
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.IS_FAVORITE + " =? ",
                    new String[]{String.valueOf(1)}, null);
        else
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.IS_FAVORITE + " =? AND " +
                    QuotesTableHelper.DESCRIPTION + " LIKE '%" + args.getString(Constants.KEY_SEARCH_STRING) +
                    "%' ", new String[]{String.valueOf(1)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuotesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuotesCursorAdapter.swapCursor(null);
    }
}
