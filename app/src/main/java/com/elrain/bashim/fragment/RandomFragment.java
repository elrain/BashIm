package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RecyclerAdapter;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.NetworkUtil;
import com.elrain.bashim.webutil.HtmlWorker;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by denys.husher on 23.11.2015.
 */
public class RandomFragment extends Fragment implements HtmlWorker.OnHtmlParsed {

    private RecyclerAdapter mRandomAdapter;
    private RecyclerView mRvItems;

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
        mRvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRvItems.setItemAnimator(itemAnimator);

        mRandomAdapter = new RecyclerAdapter(getActivity(), new ArrayList<BashItem>());
        mRvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvItems.setAdapter(mRandomAdapter);

        downloadAndParse();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_random, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aRefresh:
                downloadAndParse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void downloadAndParse() {
        NetworkUtil.isDeviceOnline(getActivity(), new NetworkUtil.OnDeviceOnlineListener() {
            @Override
            public void connected() {
                EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, RandomFragment.this));
                HtmlWorker.getRandomQuotes(RandomFragment.this, Constants.RANDOM_URL);
            }

            @Override
            public void disconnected() {
                DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAndParse();
                    }
                }).show();
            }

            @Override
            public void onlyFiWiPossible() {
                DialogsHelper.noInternetByPreferencesDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAndParse();
                    }
                }).show();
            }
        });
    }

    @Override
    public void returnResult(ArrayList<BashItem> quotes) {
        mRandomAdapter.setAdapter(quotes);
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, RandomFragment.this));
        mRvItems.scrollToPosition(0);
    }
}
