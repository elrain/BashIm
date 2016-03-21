package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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

import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.ContextMenuListener;
import com.elrain.bashim.util.DateUtil;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {

    private List<BashItem> mItems;
    private final Context mContext;
    private final BashPreferences mBashPreferences;
    private final BriteDatabase mDb;
    private boolean mFavorite;

    public CommonAdapter(Context context, BashPreferences mBashPreferences, BriteDatabase briteDatabase) {
        this.mContext = context;
        this.mBashPreferences = mBashPreferences;
        this.mDb = briteDatabase;
        if (null == mItems)
            mItems = new ArrayList<>();
    }

    public CommonAdapter(Context context, BashPreferences mBashPreferences, BriteDatabase briteDatabase,
                         boolean isFavorite) {
        this.mContext = context;
        this.mBashPreferences = mBashPreferences;
        this.mFavorite = isFavorite;
        this.mDb = briteDatabase;
        if (null == mItems)
            mItems = new ArrayList<>();
    }

    public void addItems(List<BashItem> items) {
        mItems.clear();
        mItems.addAll(items);
        this.notifyDataSetChanged();
    }

    public void clearList(){
        mItems.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final boolean isFavorite = getItem(position).isFavorite();
        final boolean isAuthorNonNull = !TextUtils.isEmpty(getItem(position).getAuthor());
        final String link = getItem(position).getLink();
        holder.setLink(link);
        final String title = getItem(position).getTitle();
        final long id = getItemId(position);
        holder.tvPubDate.setText(DateUtil.getItemPubDate(getItem(position).getPubDate()));
        if (isFavorite)
            holder.ivFavorite.setImageResource(android.R.drawable.star_big_on);
        else
            holder.ivFavorite.setImageResource(android.R.drawable.star_big_off);
        holder.ivFavorite.setOnClickListener(v -> QuotesTableHelper.makeFavorite(mDb, id, !isFavorite));

        if (isAuthorNonNull) {
            holder.tvText.setVisibility(View.GONE);
            holder.ivComics.setVisibility(View.VISIBLE);
            final String url = getItem(position).getDescription();
            holder.setText(url, getItem(position).getAuthor());
            holder.makeClick(getItemId(position), !mFavorite);
            Picasso.with(mContext).load(url).config(Bitmap.Config.ALPHA_8).into(holder.ivComics);
            holder.tvTitle.setText(getItem(position).getAuthor());
        } else {
            holder.tvText.setVisibility(View.VISIBLE);
            holder.ivComics.setVisibility(View.GONE);
            String description = getItem(position).getDescription();
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
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    public BashItem getItem(int position) {
        return mItems.get(position);
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
            mContextMenuListener = new ContextMenuListener(mContext, false);
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
            ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE, Color.YELLOW});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.ITALIC, -1, blueColor, null);
            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        }
        return new SpannableString(text);
    }
}
