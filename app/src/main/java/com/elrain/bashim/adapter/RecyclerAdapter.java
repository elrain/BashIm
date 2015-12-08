package com.elrain.bashim.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elrain.bashim.R;
import com.elrain.bashim.object.BashItem;
import com.elrain.bashim.util.DateUtil;
import com.elrain.bashim.util.ContextMenuListener;

import java.util.ArrayList;

/**
 * Created by denys.husher on 07.12.2015.
 */
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item_view,
                parent, false);
        return new ViewHolder(v);
    }

    public BashItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvPubDate.setText(DateUtil.getItemPubDate(getItem(position).getPubDate()));
        holder.tvTitle.setText(getItem(position).getTitle());
        holder.setText(Html.fromHtml(getItem(position).getDescription()));
        holder.setLink(getItem(position).getLink());
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getItem(position).getLink()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPubDate;
        private final TextView tvText;
        final TextView tvTitle;
        private ContextMenuListener mContextMenuListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPubDate = (TextView) itemView.findViewById(R.id.tvBashItemPubDate);
            tvText = (TextView) itemView.findViewById(R.id.tvBashItemText);
            tvTitle = (TextView) itemView.findViewById(R.id.tvBashItemTitle);
            itemView.findViewById(R.id.ivComics).setVisibility(View.GONE);
            itemView.findViewById(R.id.ivFavorite).setVisibility(View.INVISIBLE);
            mContextMenuListener = new ContextMenuListener(mContext, true);
            itemView.setOnCreateContextMenuListener(mContextMenuListener);
        }

        public void setLink(String link) {
            mContextMenuListener.setLink(link);
        }

        public void setText(Spanned text){
            tvText.setText(text);
            mContextMenuListener.setText(text.toString());
        }
    }
}
