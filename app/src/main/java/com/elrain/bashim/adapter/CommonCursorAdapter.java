package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.ContextMenuListener;
import com.elrain.bashim.util.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by denys.husher on 12.11.2015.
 */
public class CommonCursorAdapter extends CursorAdapter {

    public CommonCursorAdapter(Context context) {
        super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder holder = new ViewHolder(context, view);
        final boolean isFavorite = cursor.getInt(cursor.getColumnIndex(QuotesTableHelper.IS_FAVORITE)) == 1;
        final boolean isAuthorNonNull = null != cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR));
        final String link = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.LINK));
        final String title = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.TITLE));
        final long id = cursor.getLong(cursor.getColumnIndex(QuotesTableHelper.ID));
        holder.tvPubDate.setText(DateUtil.getItemPubDate(new Date(cursor.getLong(
                cursor.getColumnIndex(QuotesTableHelper.PUB_DATE)))));
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

        if (isAuthorNonNull) {
            holder.tvText.setVisibility(View.GONE);
            holder.ivComics.setVisibility(View.VISIBLE);
            final String url = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION));
            holder.setLink(url);
            Picasso.with(context).load(url).into(holder.ivComics);
            holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
            holder.setAuthor(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
        } else {
            holder.setLink(link);
            holder.tvText.setVisibility(View.VISIBLE);
            holder.ivComics.setVisibility(View.GONE);
            holder.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION))));
            holder.tvTitle.setText(title);
            holder.setAuthor(null);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    context.startActivity(intent);
                }
            });
        }
    }

    private static class ViewHolder {
        final ImageView ivComics;
        final ImageView ivFavorite;
        final TextView tvPubDate;
        final TextView tvText;
        final TextView tvTitle;
        private ContextMenuListener mContextMenuListener;

        public ViewHolder(Context context, View v) {
            ivComics = (ImageView) v.findViewById(R.id.ivComics);
            tvPubDate = (TextView) v.findViewById(R.id.tvBashItemPubDate);
            ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);
            tvText = (TextView) v.findViewById(R.id.tvBashItemText);
            tvTitle = (TextView) v.findViewById(R.id.tvBashItemTitle);
            mContextMenuListener = new ContextMenuListener(context, false);
            v.setOnCreateContextMenuListener(mContextMenuListener);
        }

        public void setLink(String link) {
            mContextMenuListener.setLink(link);
        }

        public void setAuthor(String author) {
            mContextMenuListener.setAuthor(author);
        }

        public void setText(Spanned text){
            tvText.setText(text);
            mContextMenuListener.setText(text.toString());
        }
    }
}
