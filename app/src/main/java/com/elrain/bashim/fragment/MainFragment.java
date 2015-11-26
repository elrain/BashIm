package com.elrain.bashim.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.elrain.bashim.BashContentProvider;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.CommonCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.fragment.helper.PostQuotListener;
import com.elrain.bashim.fragment.helper.SearchHelper;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by denys.husher on 05.11.2015.
 */
public class MainFragment extends Fragment implements ServiceConnection,
        LoaderManager.LoaderCallbacks<Cursor> {

    private boolean isBound = false;
    private BashService mBashService;
    private CommonCursorAdapter mQuotesCursorAdapter;
    private AlertDialog mNoInternetDialog;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_STARTED))
                    onDownloadStarted();
                else if (intent.getAction().equals(Constants.ACTION_DOWNLOAD_FINISHED))
                    onDownloadFinished();
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
        mQuotesCursorAdapter = new CommonCursorAdapter(getActivity());
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnItemLongClickListener(new PostQuotListener(getActivity()));
        getLoaderManager().initLoader(Constants.ID_LOADER, null, MainFragment.this);
        initRssDownloading();
    }

    private void initRssDownloading() {
        if (!NetworkUtil.isDeviceOnline(getActivity())) {
            EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, this));
            mNoInternetDialog = DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    initRssDownloading();
                }
            });
            mNoInternetDialog.show();
        } else if (isBound)
            mBashService.downloadXml();
        else downloadRss();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchHelper(getActivity(), this));
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
        if (null != mNoInternetDialog && mNoInternetDialog.isShowing())
            mNoInternetDialog.dismiss();
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, this));
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_STARTED));
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Constants.ACTION_DOWNLOAD_FINISHED));
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
    }

    private void onDownloadFinished() {
        if (isBound && null != getActivity()) {
            getActivity().unbindService(this);
            isBound = false;
        }
        if (null != getActivity())
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getLoaderManager().restartLoader(Constants.ID_LOADER, null, MainFragment.this);
                    EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED,
                            MainFragment.this));
                }
            });
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == args)
            return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                    QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NULL ", null, null);
        else return new CursorLoader(getActivity(), BashContentProvider.QUOTES_CONTENT_URI,
                QuotesTableHelper.MAIN_SELECTION, QuotesTableHelper.AUTHOR + " IS NULL "
                + " AND " + QuotesTableHelper.DESCRIPTION + " LIKE '%"
                + args.getString(Constants.KEY_SEARCH_STRING) + "%'", null, null);
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
