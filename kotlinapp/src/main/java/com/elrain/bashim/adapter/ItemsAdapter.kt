package com.elrain.bashim.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.elrain.bashim.adapter.BaseAdapter
import com.elrain.bashim.R
import com.elrain.bashim.dao.BashItem
import com.elrain.bashim.utils.DateUtils

class ItemsAdapter(cursor: Cursor?) : BaseAdapter<ItemsAdapter.ViewHolder>(cursor) {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.quotes_adapter_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, bashItem: BashItem) {
        holder.tvBashItemPubDate.text = DateUtils.getItemPubDate(bashItem.pubDate)
        holder.tvBashItemText.text = Html.fromHtml(bashItem.description)
        holder.tvBashItemTitle.text = bashItem.title
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val tvBashItemText = v.findViewById(R.id.tvBashItemText) as TextView
        val tvBashItemPubDate = v.findViewById(R.id.tvBashItemPubDate) as TextView
        val tvBashItemTitle = v.findViewById(R.id.tvBashItemTitle) as TextView

    }

}
