package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.adapter.CommonCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.PostQuotListener;
import com.elrain.bashim.service.BashService;

/**
 * Created by denys.husher on 12.11.2015.
 */
public class CommicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_LOADER = 2205;
    private CommonCursorAdapter mComicsCursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().startService(new Intent(getActivity(), BashService.class));
        mComicsCursorAdapter = new CommonCursorAdapter(getActivity(), null);
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mComicsCursorAdapter);
        lvItems.setOnItemLongClickListener(new PostQuotListener(getActivity()));
        getLoaderManager().initLoader(ID_LOADER, null, CommicsFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                        QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE,
                        QuotesTableHelper.AUTHOR},                QuotesTableHelper.AUTHOR + " IS NOT NULL", null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mComicsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mComicsCursorAdapter.swapCursor(null);
    }
}
