package com.elrain.bashim.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.elrain.bashim.R;
import com.elrain.bashim.activity.helper.DialogsHelper;
import com.elrain.bashim.adapter.RandomAdapter;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.HtmlParser;
import com.elrain.bashim.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by denys.husher on 24.11.2015.
 */
public class BestFragment extends Fragment implements HtmlParser.OnHtmlParsed,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String YEAR = "year/";
    private static final String MONTH = "month/";
    private static final String DIVIDER = "/";
    private RandomAdapter mRandomAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);
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
        downloadAndParse(Constants.BEST_URL);
    }

    private void downloadAndParse(final String url) {
        if (NetworkUtil.isDeviceOnline(getActivity())) {
            mSwipeRefreshLayout.setRefreshing(true);
            HtmlParser.getRandomQuotes(this, url);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            DialogsHelper.noInternetDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadAndParse(url);
                }
            }).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_best, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.aDatePicker) {
            datePickerDialog().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AlertDialog datePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker_view, null);
        builder.setView(view);
        final Spinner spYears = (Spinner) view.findViewById(R.id.spYear);
        final Spinner spMonth = (Spinner) view.findViewById(R.id.spMonth);

        final String[] years = getResources().getStringArray(R.array.years);
        final String[] months = getResources().getStringArray(R.array.months);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYears.setAdapter(yearAdapter);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(monthAdapter);
        builder.setTitle(R.string.dialog_title_date_picker);
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
        mRandomAdapter.setAdapter(quotes);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
    }
}
