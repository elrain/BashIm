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
                    val linkText = tagA.attr("abs:href")
                    item.link = linkText.substring(0, linkText.lastIndexOf('/'))
                    val titleText = tagA.text()
                    item.title = "Цитата" +
                            titleText.substring(titleText.indexOf(']')+1, titleText.length)
                    item.description = it.select("div.text").html()
                    bashItems.add(item)
                }
        return bashItems
    }
}