package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FavoriteFragment extends Fragment {

    private CommonAdapter mQuotesCursorAdapter;
    @Inject
    BashPreferences mBashPreferences;
    @Inject
    BriteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        ((BashApp) getActivity().getApplication()).getComponent().plus().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuotesCursorAdapter = new CommonAdapter(getActivity(), mBashPreferences, mDb, true);
        RecyclerView lvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        lvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvItems.setAdapter(mQuotesCursorAdapter);

        QuotesTableHelper.getBashItems(Constants.QueryFilter.FAVORITE, mDb,
                mBashPreferences.getSearchFilter(), 0)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    if (items.size() == 0 && !isDetached() && null != getActivity()) {
                        Toast.makeText(getActivity(), getString(R.string.error_no_items_found),
                                Toast.LENGTH_SHORT).show();
                        mQuotesCursorAdapter.clearList();
                    } else mQuotesCursorAdapter.addItems(items);
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBashPreferences.setFilterListener(
                () -> QuotesTableHelper.getBashItems(Constants.QueryFilter.FAVORITE, mDb,
                        mBashPreferences.getSearchFilter(), 0)
                        .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mQuotesCursorAdapter::addItems));
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
            searchView.setOnQueryTextListener(new SearchHelper(mBashPreferences));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
