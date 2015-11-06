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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.adapter.QuotesCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 05.11.2015.
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemLongClickListener {

    private QuotesCursorAdapter mQuotesCursorAdapter;
    private static final int ID_LOADER = 2204;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuotesCursorAdapter = new QuotesCursorAdapter(getActivity(), null);
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnItemLongClickListener(this);
        getLoaderManager().initLoader(ID_LOADER, null, FavoriteFragment.this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    getActivity().getLoaderManager().restartLoader(ID_LOADER, null, FavoriteFragment.this);
                    return false;
                } else if (newText.length() < 3)
                    return false;
                else {
                    Bundle b = new Bundle();
                    b.putString(Constants.KEY_SEARCH_STRING, newText);
                    getActivity().getLoaderManager().restartLoader(ID_LOADER, b, FavoriteFragment.this);
                    return true;
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == args)
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                            QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE},
                    QuotesTableHelper.IS_FAVORITE + " =?", new String[]{String.valueOf(1)}, null);
        else
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                            QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE},
                    QuotesTableHelper.IS_FAVORITE + " =? AND " + QuotesTableHelper.DESCRIPTION
                            + " LIKE '%" + args.getString(Constants.KEY_SEARCH_STRING) + "%'",
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(Constants.TEXT_PLAIN);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(QuotesTableHelper.getText(getActivity(), id)).toString());
        startActivity(sharingIntent);
        return true;
    }
}
