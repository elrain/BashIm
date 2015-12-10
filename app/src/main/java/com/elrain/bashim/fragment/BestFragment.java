package com.elrain.bashim.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RecyclerAdapter;
import com.elrain.bashim.message.RefreshMessage;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.HtmlParser;
import com.elrain.bashim.util.NetworkUtil;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by denys.husher on 24.11.2015.
 */
public class BestFragment extends Fragment implements HtmlParser.OnHtmlParsed {

    private static final String YEAR = "year/";
    private static final String MONTH = "month/";
    private static final String DIVIDER = "/";
    private RecyclerAdapter mBestAdapter;
    private RecyclerView mRvItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvItems = (RecyclerView) view.findViewById(R.id.lvBashItems);
        mBestAdapter = new RecyclerAdapter(getActivity(), new ArrayList<BashItem>());
        mRvItems.setAdapter(mBestAdapter);
        mRvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        downloadAndParse(Constants.BEST_URL);
    }

    private void downloadAndParse(final String url) {
        NetworkUtil.isDeviceOnline(getActivity(), new NetworkUtil.OnDeviceOnlineListener() {
            @Override
            public void connected() {
                EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.STARTED, BestFragment.this));
                HtmlParser.getRandomQuotes(BestFragment.this, url);
            }

            @Override
            public void disconnected() {
                DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAndParse(url);
                    }
                }).show();
            }

            @Override
            public void onlyFiWiPossible() {
                DialogsHelper.noInternetByPreferencesDialog(getActivity(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAndParse(url);
                    }
                }).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_best, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aDatePicker:
                datePickerDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog datePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker_view, null);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title_date_picker);

        final Spinner spYears = (Spinner) view.findViewById(R.id.spYear);
        final Spinner spMonth = (Spinner) view.findViewById(R.id.spMonth);
        final String[] years = getResources().getStringArray(R.array.years);
        final String[] months = getResources().getStringArray(R.array.months);

        final ArrayAdapter<String>yearAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYears.setAdapter(yearAdapter);

        final ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(monthAdapter);

        builder.setPositiveButton(R.string.dialog_btn_text_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == spMonth.getSelectedItemPosition())
                    downloadAndParse(Constants.BEST_URL + YEAR + years[spYears.getSelectedItemPosition()]);
                else
                    downloadAndParse(Constants.BEST_URL + MONTH
                            + years[spYears.getSelectedItemPosition()] + DIVIDER
                            + spMonth.getSelectedItemPosition());

            }
        });
        return builder.create();
    }

    @Override
    public void returnResult(ArrayList<BashItem> quotes) {
        mBestAdapter.setAdapter(quotes);
        EventBus.getDefault().post(new RefreshMessage(RefreshMessage.State.FINISHED, BestFragment.this));
        mRvItems.scrollToPosition(0);
    }
}
