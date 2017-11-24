package com.elrain.bashim.main

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.elrain.bashim.R
import com.elrain.bashim.entities.BashItem
import com.elrain.bashim.utils.DateUtils
import com.squareup.picasso.Picasso

/**
 * Created by elrain on 28.10.17.
 */
class ItemAdapter(val context: Context, val onItemAction: OnItemAction) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var itemsList: List<BashItem> = mutableListOf()

    fun setItems(items: List<BashItem>) {
        itemsList = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = itemsList.size

    fun getItemByPosition(position: Int): BashItem = itemsList[position]

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.quotes_adapter_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bashItem = getItemByPosition(position)
        if (bashItem.isQuote()) {
            holder.ivComics.visibility = View.GONE
            holder.tvBashItemText.visibility = View.VISIBLE
            holder.tvBashItemText.text = Html.fromHtml(bashItem.description)
        } else {
            holder.ivComics.visibility = View.VISIBLE
            holder.tvBashItemText.visibility = View.GONE
            Picasso.with(context)
                    .load(bashItem.description)
                    .config(Bitmap.Config.ALPHA_8)
                    .into(holder.ivComics)
        }
        holder.tvBashItemTitle.text = bashItem.title
        holder.tvBashItemPubDate.text = DateUtils.getItemPubDate(bashItem.pubDate)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvBashItemText: TextView = v.findViewById(R.id.tvBashItemText)
        val tvBashItemPubDate: TextView = v.findViewById(R.id.tvBashItemPubDate)
        val tvBashItemTitle: TextView = v.findViewById(R.id.tvBashItemTitle)
        val ivComics: ImageView = v.findViewById(R.id.ivComics)

        init {
            v.setOnLongClickListener {
                onItemAction.shareItem(getItemByPosition(adapterPosition))
                true
            }
            tvBashItemTitle.setOnClickListener {
                onItemAction.openInTab(getItemByPosition(adapterPosition).link)
            }
        }
    }
}