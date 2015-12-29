package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.ContextMenuListener;
import com.elrain.bashim.util.DateUtil;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<BashItem> mItems;

    public RecyclerAdapter(Context context, ArrayList<BashItem> items) {
        this.mItems = items;
        this.mContext = context;
    }

    public void setAdapter(ArrayList<BashItem> items) {
        mItems = items;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,
                parent, false);
        return new ViewHolder(v);
    }

    private BashItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvPubDate.setText(DateUtil.getItemPubDate(getItem(position).getPubDate()));
        holder.tvTitle.setText(getItem(position).getTitle());
        holder.setText(getItem(position).getDescription());
        holder.setLink(getItem(position).getLink());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getItem(position).getLink()));
                mContext.startActivity(intent);
            }
        });

        boolean isFavorite = QuotesTableHelper.isFavorite(mContext, getItem(position).getLink());

        if (isFavorite) holder.ivFavorite.setImageResource(android.R.drawable.star_big_on);
        else holder.ivFavorite.setImageResource(android.R.drawable.star_big_off);

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuotesTableHelper.makeOrInsertAsFavorite(mContext, getItem(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPubDate;
        private final TextView tvText;
        private final TextView tvTitle;
        private final ImageView ivFavorite;
        private final ContextMenuListener mContextMenuListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPubDate = (TextView) itemView.findViewById(R.id.tvBashItemPubDate);
            tvText = (TextView) itemView.findViewById(R.id.tvBashItemText);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBashItemTitle);
            itemView.findViewById(R.id.ivComics).setVisibility(View.GONE);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            mContextMenuListener = new ContextMenuListener(mContext, true);
            itemView.setOnCreateContextMenuListener(mContextMenuListener);
        }

        public void setLink(String link) {
            mContextMenuListener.setLink(link);
        }

        public void setText(String text) {
            tvText.setText(text);
            mContextMenuListener.setTextAndAuthor(text, null);
        }
    }
}
