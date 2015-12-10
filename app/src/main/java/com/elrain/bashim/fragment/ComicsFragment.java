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
import android.widget.AdapterView;
import android.widget.ListView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImageScaleActivity;
import com.elrain.bashim.adapter.CommonCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.ContextMenuListener;

/**
 * Created by denys.husher on 12.11.2015.
 */
public class ComicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private CommonCursorAdapter mComicsCursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContextMenuListener menuListener = new ContextMenuListener(getActivity(), false);
        mComicsCursorAdapter = new CommonCursorAdapter(getActivity());
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mComicsCursorAdapter);
        lvItems.setOnItemClickListener(this);
        lvItems.setOnItemLongClickListener(menuListener);
        lvItems.setOnCreateContextMenuListener(menuListener);
        getLoaderManager().initLoader(Constants.ID_LOADER, null, ComicsFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NOT NULL ",
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mComicsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mComicsCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
        intent.putExtra(Constants.KEY_INTENT_IMAGE_ID, id);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
