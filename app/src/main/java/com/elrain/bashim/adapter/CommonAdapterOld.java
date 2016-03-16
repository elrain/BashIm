package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.ContextMenuListener;
import com.elrain.bashim.util.DateUtil;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class CommonAdapterOld extends RecyclerCursorAdapter<CommonAdapterOld.ViewHolder> {

    private boolean mFavorite;
    @Inject BashPreferences mBashPreferences;
    @Inject BriteDatabase mDb;

    public CommonAdapterOld(Context context) {
        super(context, null);
        ((BashApp)context.getApplicationContext()).getComponent().inject(this);
    }

    public CommonAdapterOld(Context context, boolean isFavorite) {
        super(context, null);
        mFavorite = isFavorite;
        ((BashApp)context.getApplicationContext()).getComponent().inject(this);
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
        holder.ivFavorite.setOnClickListener(v -> QuotesTableHelper.makeFavorite(mDb, id, !isFavorite));

        if (isAuthorNonNull) {
            holder.tvText.setVisibility(View.GONE);
            holder.ivComics.setVisibility(View.VISIBLE);
            final String url = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION));
            holder.setText(url, cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
            if (mFavorite) holder.makeClick(0, false);
            else
                holder.makeClick(cursor.getLong(cursor.getColumnIndex(QuotesTableHelper.ID)), true);
            Picasso.with(getContext()).load(url).config(Bitmap.Config.ALPHA_8).into(holder.ivComics);
            holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(QuotesTableHelper.AUTHOR)));
        } else {
            holder.tvText.setVisibility(View.VISIBLE);
            holder.ivComics.setVisibility(View.GONE);
            String description = cursor.getString(cursor.getColumnIndex(QuotesTableHelper.DESCRIPTION));
            if (!description.contains("\n")) {
                Spanned text = Html.fromHtml(description);
                holder.tvText.setText(highlightTextileNeeded(text.toString()));
                holder.setText(text.toString(), null);
            } else {
                holder.tvText.setText(highlightTextileNeeded(description));
                holder.setText(description, null);
            }
            holder.tvTitle.setText(title);
            holder.tvTitle.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                getContext().startActivity(intent);
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

    private Spanned highlightTextileNeeded(String text) {
        String filter = mBashPreferences.getSearchFilter();
        if (!TextUtils.isEmpty(filter)) {
            int startPos = text.toLowerCase(Locale.US).indexOf(filter.toLowerCase(Locale.US));
            int endPos = startPos + filter.length();
            Spannable spannable = new SpannableString(text);
            ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.ITALIC, -1, blueColor, null);
            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        return new SpannableString(text);
    }
}
