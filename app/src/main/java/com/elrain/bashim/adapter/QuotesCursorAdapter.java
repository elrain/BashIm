package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.DateUtil;

import java.util.Date;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class QuotesCursorAdapter extends CursorAdapter {

    public QuotesCursorAdapter(Context context, Cursor c) {
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.bash_item_view, parent, false);
        ViewHolder.initViews(v);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvText.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION))));
        holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.TITLE)));
        final String link = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.LINK));
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            }
        });
        holder.tvPubDate.setText(DateUtil.getItemPubDate(new Date(cursor.getLong(cursor.getColumnIndex(QuotesTableHelper.PUB_DATE)))));
        final boolean isFavorite = cursor.getInt(cursor.getColumnIndex(QuotesTableHelper.IS_FAVORITE)) == 1;
        final long id = cursor.getLong(cursor.getColumnIndex(QuotesTableHelper.ID));
        if (isFavorite)
            holder.ivFavorite.setImageResource(android.R.drawable.star_big_on);
        else
            holder.ivFavorite.setImageResource(android.R.drawable.star_big_off);
        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuotesTableHelper.makeFavorite(context, id, !isFavorite);
            }
        });
    }

    private static class ViewHolder {
        TextView tvText;
        TextView tvTitle;
        TextView tvPubDate;
        ImageView ivFavorite;

        public static void initViews(View v) {
            ViewHolder vh = new ViewHolder();
            vh.tvText = (TextView) v.findViewById(R.id.tvBashItemText);
            vh.tvTitle = (TextView) v.findViewById(R.id.tvBashItemTitle);
            vh.tvPubDate = (TextView) v.findViewById(R.id.tvBashItemPubDate);
            vh.ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);
            v.setTag(vh);
        }
    }
}
