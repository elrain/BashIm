package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RandomAdapter;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.HtmlParser;
import com.elrain.bashim.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by denys.husher on 23.11.2015.
 */
public class RandomFragment extends Fragment implements HtmlParser.OnHtmlParsed,
        SwipeRefreshLayout.OnRefreshListener {

    private RandomAdapter mRandomAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.lvBashItems);
        mRandomAdapter = new RandomAdapter(getActivity(), new ArrayList<BashItem>());
        listView.setAdapter(mRandomAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType(Constants.TEXT_PLAIN);
                RandomAdapter.ViewHolder holder = (RandomAdapter.ViewHolder) view.getTag();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        Html.fromHtml(String.format(Constants.SHARE_FORMATTER, holder.tvText.getText(),
                                holder.link)).toString());
                startActivity(sharingIntent);
                return true;
            }
        });

        downloadAndParse();
    }

    private void downloadAndParse() {
        if (NetworkUtil.isDeviceOnline(getActivity())) {
            mSwipeRefreshLayout.setRefreshing(true);
            HtmlParser.getRandomQuotes(this, Constants.RANDOM_URL);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadAndParse();
                }
            }).show();
        }

    }


    @Override
    public void returnResult(ArrayList<BashItem> quotes) {
        mRandomAdapter.setAdapter(quotes);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        downloadAndParse();
    }
}
