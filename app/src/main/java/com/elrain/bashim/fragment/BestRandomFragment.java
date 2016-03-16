package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RecyclerAdapter;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;
import com.elrain.bashim.webutil.HtmlWorker;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by denys.husher on 24.11.2015.
 * Fragment for showing http://bash.im/best
 */
public class BestRandomFragment extends Fragment implements HtmlWorker.OnHtmlParsed {

    private RecyclerAdapter mBestAdapter;
    private RecyclerView mRvItems;
    private Bundle mData;
    @Inject
    BriteDatabase mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BashApp)getActivity().getApplication()).getComponent().inject(this);
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
        mRvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        mBestAdapter = new RecyclerAdapter(getActivity(), mDb, new ArrayList<>());
        mRvItems.setAdapter(mBestAdapter);
        mRvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        downloadAndParse(mData.getString(Constants.PARSE));
    }

    private void downloadAndParse(final String url) {
        NetworkUtil.isDeviceOnline(getActivity(), new NetworkUtil.OnDeviceOnlineListener() {
            @Override
            public void connected() {
                EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED,
                        BestRandomFragment.this));
                HtmlWorker.getQuotes(BestRandomFragment.this, url);
            }

            @Override
            public void disconnected() {
                DialogsHelper.noInternetDialog(getActivity(),
                        (dialog, which) -> downloadAndParse(url)).show();
            }

            @Override
            public void onlyWiFiPossible() {
                DialogsHelper.noInternetByPreferencesDialog(getActivity(),
                        (dialog, which) -> downloadAndParse(url)).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_best, menu);
        if (Constants.RANDOM_URL.equals(mData.getString(Constants.PARSE))) {
            menu.findItem(R.id.aDatePicker).setVisible(false);
            menu.findItem(R.id.aRefresh).setVisible(true);
        } else if (Constants.BEST_URL.equals(mData.getString(Constants.PARSE))) {
            menu.findItem(R.id.aDatePicker).setVisible(true);
            menu.findItem(R.id.aRefresh).setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aDatePicker:
                DialogsHelper.datePickerDialog(getActivity(), this::downloadAndParse).show();
                return true;
            case R.id.aRefresh:
                downloadAndParse(Constants.RANDOM_URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void returnResult(List<BashItem> quotes) {
        mBestAdapter.addItems(quotes);
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED,
                BestRandomFragment.this));
        mRvItems.scrollToPosition(0);
    }

    @Override
    public void setArguments(Bundle args) {
        mData = args;
    }
}
