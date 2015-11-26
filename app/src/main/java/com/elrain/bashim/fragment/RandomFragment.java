package com.elrain.bashim.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RandomAdapter;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.HtmlParser;
import com.elrain.bashim.util.NetworkUtil;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by denys.husher on 23.11.2015.
 */
public class RandomFragment extends Fragment implements HtmlParser.OnHtmlParsed {

    private RandomAdapter mRandomAdapter;

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
        if (NetworkUtil.isDeviceOnline(getActivity())) {
            EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, this));
            HtmlParser.getRandomQuotes(this, Constants.RANDOM_URL);
        } else {
            EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, this));
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
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, this));
    }
}
