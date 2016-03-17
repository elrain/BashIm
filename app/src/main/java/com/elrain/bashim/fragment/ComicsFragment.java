package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.ImagePagerActivity;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by denys.husher on 12.11.2015.
 * Fragment for showing http://bash.im/comics
 */
public class ComicsFragment extends Fragment {

    private CommonAdapter mComicsCursorAdapter;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private Subscription mSubscription;
    @Inject
    BashPreferences mBashPreferences;
    @Inject
    BriteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BashApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_comics_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mComicsCursorAdapter = new CommonAdapter(getActivity(),mBashPreferences, mDb, false);
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

                if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + 2)) {
                    QuotesTableHelper.getBashItems(Constants.QueryFilter.COMICS, mDb, null,
                            (mComicsCursorAdapter.getItemCount() + 7))
                            .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mComicsCursorAdapter::addItems);
                }
            }
        });
        mSubscription = QuotesTableHelper.getBashItems(Constants.QueryFilter.COMICS, mDb, null,
                (mComicsCursorAdapter.getItemCount() + 7))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(mComicsCursorAdapter::addItems);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }
}
