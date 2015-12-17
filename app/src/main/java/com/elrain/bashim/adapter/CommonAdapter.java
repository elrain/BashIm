package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.ContextMenuListener;
import com.elrain.bashim.util.DateUtil;

import java.util.Date;

/**
 * Created by denys.husher on 17.12.2015.
 */
public class CommonAdapter extends RecyclerCursorAdapter<CommonAdapter.ViewHolder> {

    private boolean mFavorite;

    public CommonAdapter(Context context) {
        super(context, null);
    }

    public CommonAdapter(Context context, boolean isFavorite) {
        super(context, null);
        mFavorite = isFavorite;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        final boolean isFavorite = cursor.getInt(cursor.getColumnIndex(QuotesTableHelper.IS_FAVORITE)) == 1;
        final boolean isAuthorNonNull = null != cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR));
        final String link = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.LINK));
        holder.setLink(link);
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
                QuotesTableHelper.makeFavorite(getContext(), id, !isFavorite);
            }
        });

        if (isAuthorNonNull) {
            holder.tvText.setVisibility(View.GONE);
            holder.ivComics.setVisibility(View.VISIBLE);
            final String url = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION));
            holder.setText(url, cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
            if (mFavorite) holder.makeClick(0, false);
            else
                holder.makeClick(cursor.getLong(cursor.getColumnIndex(QuotesTableHelper.ID)), true);
            Glide.with(getContext()).load(url).into(holder.ivComics);
            holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
        } else {
            holder.tvText.setVisibility(View.VISIBLE);
            holder.ivComics.setVisibility(View.GONE);
            Spanned text = Html.fromHtml(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION)));
            holder.tvText.setText(text);
            holder.setText(text.toString(), null);
            holder.tvTitle.setText(title);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_view, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivComics;
        final ImageView ivFavorite;
        final TextView tvPubDate;
        final TextView tvText;
        final TextView tvTitle;
        final View mView;
        private final ContextMenuListener mContextMenuListener;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            ivComics = (ImageView) v.findViewById(R.id.ivComics);
            tvPubDate = (TextView) v.findViewById(R.id.tvBashItemPubDate);
            ivFavorite = (ImageView) v.findViewById(R.id.ivFavorite);
            tvText = (TextView) v.findViewById(R.id.tvBashItemText);
            tvTitle = (TextView) v.findViewById(R.id.tvBashItemTitle);
            mContextMenuListener = new ContextMenuListener(getContext(), false);
            v.setOnCreateContextMenuListener(mContextMenuListener);
        }

        public void setLink(String link) {
            mContextMenuListener.setLink(link);
        }

        public void setText(String text, String author) {
            mContextMenuListener.setTextAndAuthor(text, author);
        }

        public void makeClick(long id, boolean isGalleryNeeded) {
            mContextMenuListener.addClickListener(mView, id, isGalleryNeeded);
        }

    }
}
