package com.elrain.bashim.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.activity.helper.NotificationHelper;
import com.elrain.bashim.adapter.QuotesCursorAdapter;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.service.BashService;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;
import com.elrain.bashim.util.NewQuosCounter;

/**
 * Created by denys.husher on 05.11.2015.
 */
public class MainFragment extends Fragment implements BashService.DownloadListener,
        ServiceConnection, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final int ID_LOADER = 2203;
    private boolean isBound = false;
    private BashService mBashService;
    private QuotesCursorAdapter mQuotesCursorAdapter;
    private AlertDialog mNoInternetDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        mQuotesCursorAdapter = new QuotesCursorAdapter(getActivity(), null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ListView lvItems = (ListView) view.findViewById(R.id.lvBashItems);
        lvItems.setAdapter(mQuotesCursorAdapter);
        lvItems.setOnItemLongClickListener(this);
        getLoaderManager().initLoader(ID_LOADER, null, MainFragment.this);
        if (!NetworkUtil.isDeviceOnline(getActivity())) {
            mNoInternetDialog = DialogsHelper.noInternetDialog(getActivity());
            mNoInternetDialog.show();
        } else downloadRss();
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
                if (newText.length() < 3)
                    return false;
                else {
                    Bundle b = new Bundle();
                    b.putString(Constants.KEY_SEARCH_STRING, newText);
                    getActivity().getLoaderManager().restartLoader(ID_LOADER, b, MainFragment.this);
                    return true;
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void downloadRss() {
        Intent intent = new Intent(getActivity(), BashService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDownloadStarted() {
        if (null != mNoInternetDialog && mNoInternetDialog.isShowing())
            mNoInternetDialog.dismiss();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onDownloadFinished() {
        if (isBound) {
            getActivity().unbindService(this);
            isBound = false;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getLoaderManager().restartLoader(ID_LOADER, null, MainFragment.this);
                if (!BashPreferences.getInstance(getActivity()).isFirststart()
                        && NewQuosCounter.getInstance().getCounter() != 0)
                    NotificationHelper.showNotification(getActivity());
                else NewQuosCounter.getInstance().setCounterTooZero();
                mBashService.setListener(null);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        BashService.LocalBinder binder = (BashService.LocalBinder) service;
        mBashService = binder.getService();
        mBashService.setListener(this);
        mBashService.downloadXml(true);
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == args)
            return new CursorLoader(getActivity(), BashContentProvider.QUOTS_CONTENT_URI,
                    new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                            QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE},
                    null, null, null);
        else
            return new CursorLoader(getActivity(), BashContentProvider.QUOTS_CONTENT_URI,
                    new String[]{QuotesTableHelper.ID, QuotesTableHelper.DESCRIPTION, QuotesTableHelper.TITLE,
                            QuotesTableHelper.PUB_DATE, QuotesTableHelper.LINK, QuotesTableHelper.IS_FAVORITE},
                    QuotesTableHelper.DESCRIPTION + " LIKE '%" + args.getString(Constants.KEY_SEARCH_STRING) + "%'", null, null);
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

    @Override
    public void onRefresh() {
        if (isBound)
            mBashService.downloadXml(true);
        else downloadRss();
    }
}
