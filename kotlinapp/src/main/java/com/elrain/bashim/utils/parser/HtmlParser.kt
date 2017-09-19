package com.elrain.bashim.utils.parser

import com.elrain.bashim.dao.BashItem
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*

class HtmlParser(private val mDocument: Document) : Parser {

    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)

    override fun parse(): List<BashItem> = getElements()

    private fun getElements(): List<BashItem> {
        val bashItems: MutableList<BashItem> = mutableListOf()
        mDocument.select("div.quote").filter { it.className() == "quote" }
                .forEach {
                    val item = BashItem()
                    item.pubDate = mDateFormat.parse(it.select("span.date").text())
                    val tagA = it.select("a")
                    item.link = tagA.attr("abs:href")
                    item.title = "Цитата ${tagA.text()}"
                    item.description = it.select("div.text").text()
                    bashItems.add(item)
                }
        return bashItems
    }
}