package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.CommonAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;
import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Subscription;

public class MainFragment extends Fragment implements ServiceConnection {

    private boolean isBound = false;
    private BashService mBashService;
    private CommonAdapter mQuotesCursorAdapter;
    private BroadcastReceiver mBroadcastReceiver;
    private SearchView mSearchView;
    private boolean isFirstSynced;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private Subscription mSubscription;

    @Inject
    BriteDatabase mDb;
    @Inject
    BashPreferences mBashPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((BashApp) getActivity().getApplication()).getComponent().plus().inject(this);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_STARTED))
                    onDownloadStarted();
                else if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_FINISHED))
                    onDownloadFinished();
                else if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_ABORTED))
                    onAborted();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().startService(new Intent(getActivity(), BashService.class));
        mQuotesCursorAdapter = new CommonAdapter(getActivity(), mBashPreferences, mDb);
        RecyclerView lvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        lvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + 3)) {
                    QuotesTableHelper.getBashItems(Constants.QueryFilter.QUOTE, mDb, mBashPreferences.getSearchFilter(),
                            (mQuotesCursorAdapter.getItemCount() + 10)).subscribe(mQuotesCursorAdapter::addItems);
                }
            }
        });
        if (!isFirstSynced) initRssDownloading();

        QuotesTableHelper.getBashItems(Constants.QueryFilter.QUOTE, mDb, mBashPreferences.getSearchFilter(),
                (mQuotesCursorAdapter.getItemCount() + 10))
                .subscribe(mQuotesCursorAdapter::addItems);
    }

    private void initRssDownloading() {
        NetworkUtil.isDeviceOnline(getActivity(), new NetworkUtil.OnDeviceOnlineListener() {
            @Override
            public void connected() {
                if (isBound) mBashService.downloadXml();
                else downloadRss();
            }

            @Override
            public void disconnected() {
                DialogsHelper.noInternetDialog(getActivity(), (dialog, which) -> initRssDownloading()).show();
            }

            @Override
            public void onlyWiFiPossible() {
                DialogsHelper.noInternetByPreferencesDialog(getActivity(), (dialog, which) -> initRssDownloading()).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != mSearchView) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            mSearchView.setIconifiedByDefault(false);
            mSearchView.setOnQueryTextListener(new SearchHelper(mBashPreferences));
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aRefresh:
                initRssDownloading();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadRss() {
        Intent intent = new Intent(getActivity(), BashService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void onDownloadStarted() {
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, this));
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_STARTED));
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_FINISHED));
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_ABORTED));
        mBashPreferences.setFilterListener(
                () -> QuotesTableHelper.getBashItems(Constants.QueryFilter.QUOTE,
                        mDb, mBashPreferences.getSearchFilter(), (mQuotesCursorAdapter.getItemCount() + 10))
                        .map(items -> {
                            if (items.size() == 0 && !isDetached())
                                Toast.makeText(getActivity(), getString(R.string.error_no_items_found), Toast.LENGTH_SHORT).show();
                            return items;
                        })
                        .subscribe(mQuotesCursorAdapter::addItems));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getActivity().unbindService(this);
            isBound = false;
        }
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, this));
        getActivity().unregisterReceiver(mBroadcastReceiver);
        mBashPreferences.removeFilterListener();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    private void onDownloadFinished() {
        if (isBound && null != getActivity()) {
            getActivity().unbindService(this);
            isBound = false;
        }
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED,
                MainFragment.this));
        mSubscription = QuotesTableHelper.getBashItems(Constants.QueryFilter.QUOTE, mDb,
                mBashPreferences.getSearchFilter(), (mQuotesCursorAdapter.getItemCount() + 10))
                .subscribe(mQuotesCursorAdapter::addItems);
        isFirstSynced = true;
    }

    private void onAborted() {
        DialogsHelper.abortedDownload(getActivity()).show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BashService.LocalBinder binder = (BashService.LocalBinder) service;
        mBashService = binder.getService();
        mBashService.downloadXml();
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    private void resetSearchView() {
        if (null != mSearchView)
            mSearchView.clearFocus();
    }
}
