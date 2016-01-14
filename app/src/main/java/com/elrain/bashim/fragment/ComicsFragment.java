package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.elrain.bashim.dal.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImagePagerActivity;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.Constants;

/**
 * Created by denys.husher on 12.11.2015.
 * Fragment for showing http://bash.im/comics
 */
public class ComicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private CommonAdapter mComicsCursorAdapter;
    private boolean isLoadingInProcess;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mComicsCursorAdapter = new CommonAdapter(getActivity(), false);
        RecyclerView mLvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        mLvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLvItems.setAdapter(mComicsCursorAdapter);
        mLvItems.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (!isLoadingInProcess && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {
                    getLoaderManager().restartLoader(Constants.ID_LOADER, null, ComicsFragment.this);
                    isLoadingInProcess = true;
                }
            }
        });
        getLoaderManager().initLoader(Constants.ID_LOADER, null, ComicsFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NOT NULL ",
                null, QuotesTableHelper.PUB_DATE + " DESC, ROWID LIMIT " + (mComicsCursorAdapter.getItemCount() + 7));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != mComicsCursorAdapter)
            mComicsCursorAdapter.swapCursor(data);
        isLoadingInProcess = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (null != mComicsCursorAdapter)
            mComicsCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
        intent.putExtra(Constants.KEY_INTENT_IMAGE_ID, id);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
