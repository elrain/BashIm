package com.elrain.bashim.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import com.elrain.bashim.dal.helpers.QuotesTableHelper
import com.elrain.bashim.dao.BashItem
import java.util.*

abstract class BaseAdapter<VH : RecyclerView.ViewHolder>(private val context: Context, private var mCursor: Cursor?) :
        RecyclerView.Adapter<VH>() {

    protected fun getContext(): Context {
        return context
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        val item: BashItem = BashItem()
        if (c != null) {
            val pubDate: Long? = c.getLong(c.getColumnIndex(QuotesTableHelper.PUB_DATE))
            item.pubDate = if (pubDate == null) {
                Date()
            } else {
                Date(pubDate)
            }
            item.id = c.getLong(c.getColumnIndex(QuotesTableHelper.ID))
            item.title = c.getString(c.getColumnIndex(QuotesTableHelper.TITLE))
            item.description = c.getString(c.getColumnIndex(QuotesTableHelper.DESCRIPTION))
            item.link = c.getString(c.getColumnIndex(QuotesTableHelper.LINK))
            item.author = c.getString(c.getColumnIndex(QuotesTableHelper.AUTHOR))

            onBindViewHolder(holder, item)
        }
    }

    protected abstract fun onBindViewHolder(holder: VH, bashItem: BashItem)

    override fun getItemCount(): Int {
        return if (mCursor != null) mCursor!!.count else 0
    }

    private fun getItem(position: Int): Cursor? {
        if (mCursor != null && !mCursor!!.isClosed) {
            mCursor!!.moveToPosition(position)
        }

        return mCursor
    }

    fun swapCursor(newCursor: Cursor?) {
        if (newCursor != mCursor) {
            mCursor = newCursor
            notifyDataSetChanged()
        }
    }
}
