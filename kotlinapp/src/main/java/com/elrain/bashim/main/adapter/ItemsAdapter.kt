package com.elrain.bashim.main.adapter

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.elrain.bashim.R
import com.elrain.bashim.dao.BashItem
import com.elrain.bashim.main.adapter.BaseAdapter.OnItemAction
import com.elrain.bashim.utils.DateUtils
import com.squareup.picasso.Picasso

/**
 * Adapter which binds items to RecyclerView
 *
 * @param context Application context. Must implements OnItemAction
 * @param cursor Data for binding
 *
 * @see OnItemAction
 */
class ItemsAdapter(context: Context, cursor: Cursor?)
    : BaseAdapter<ItemsAdapter.ViewHolder>(context, cursor) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.quotes_adapter_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, bashItem: BashItem) {
        if (TextUtils.isEmpty(bashItem.author)) {
            holder.ivComics.visibility = View.GONE
            holder.tvBashItemText.visibility = View.VISIBLE
            holder.tvBashItemText.text = Html.fromHtml(bashItem.description)
            holder.tvBashItemTitle.text = bashItem.title
        } else {
            holder.ivComics.visibility = View.VISIBLE
            holder.tvBashItemText.visibility = View.GONE
            Picasso.with(getContext())
                    .load(bashItem.description)
                    .config(Bitmap.Config.ALPHA_8)
                    .into(holder.ivComics)
        }
        holder.tvBashItemPubDate.text = DateUtils.getItemPubDate(bashItem.pubDate)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvBashItemText = v.findViewById(R.id.tvBashItemText) as TextView
        val tvBashItemPubDate = v.findViewById(R.id.tvBashItemPubDate) as TextView
        val tvBashItemTitle = v.findViewById(R.id.tvBashItemTitle) as TextView
        val ivComics = v.findViewById(R.id.ivComics) as ImageView

        init {
            v.setOnLongClickListener {
                mOnItemAction.shareItem(getItemByPosition(adapterPosition))
                true
            }
            tvBashItemTitle.setOnClickListener {
                mOnItemAction.openInTab(getItemByPosition(adapterPosition).link)
            }
        }
    }
}
