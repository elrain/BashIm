package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elrain.bashim.R;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by denys.husher on 23.11.2015.
 */
public class RandomAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private ArrayList<BashItem> mItems;

    public RandomAdapter(Context context, ArrayList<BashItem> items) {
        this.mItems = items;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setAdapter(ArrayList<BashItem> items) {
        mItems = items;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public BashItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_view, parent, false);
            holder.tvPubDate = (TextView) convertView.findViewById(R.id.tvBashItemPubDate);
            holder.tvText = (TextView) convertView.findViewById(R.id.tvBashItemText);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvBashItemTitle);
            convertView.findViewById(R.id.ivComics).setVisibility(View.GONE);
            convertView.findViewById(R.id.ivFavorite).setVisibility(View.INVISIBLE);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        holder.tvPubDate.setText(DateUtil.getItemPubDate(getItem(position).getPubDate()));
        holder.tvTitle.setText(getItem(position).getTitle());
        holder.tvText.setText(Html.fromHtml(getItem(position).getDescription()));
        holder.link = getItem(position).getLink();
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(holder.link));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvPubDate;
        public TextView tvText;
        public TextView tvTitle;
        public String link;
    }
}
