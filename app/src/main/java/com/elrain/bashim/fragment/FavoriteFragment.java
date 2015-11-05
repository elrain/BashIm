package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.adapter.QuotesCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;

/**
 * Created by denys.husher on 05.11.2015.
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemLongClickListener {

    private QuotesCursorAdapter mQuotesCursorAdapter;
    public static final int ID_LOADER = 2204;
    public static final String TEXT_PLAIN = "text/plain";

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BashContentProvider.QUOTS_CONTENT_URI,
                new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                        QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE},
                QuotesTableHelper.IS_FAVORITE + " =?", new String[]{String.valueOf(1)}, null);
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
        sharingIntent.setType(TEXT_PLAIN);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(QuotesTableHelper.getText(getActivity(), id)).toString());
        startActivity(sharingIntent);
        return true;
    }
}
