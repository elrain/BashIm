package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
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

import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImageScaleActivity;
import com.elrain.bashim.adapter.CommonCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.CommonLoader;
import com.elrain.bashim.fragment.helper.PostQuotListener;
import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 12.11.2015.
 */
public class CommicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private CommonCursorAdapter mComicsCursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mComicsCursorAdapter = new CommonCursorAdapter(getActivity());
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mComicsCursorAdapter);
        lvItems.setOnItemLongClickListener(new PostQuotListener(getActivity()));
        lvItems.setOnItemClickListener(this);
        getLoaderManager().initLoader(Constants.ID_LOADER, null, CommicsFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return CommonLoader.getInstance(getActivity()).getComics().build();
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
        intent.putExtra(Constants.KEY_INTENT_IMAGE_URL, QuotesTableHelper.getUrlForComicsById(getActivity(), id));
        getActivity().startActivity(intent);
    }
}
