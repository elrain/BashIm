package com.elrain.bashim.utils.parser

import com.elrain.bashim.dao.BashItem
import com.elrain.bashim.utils.DateUtils
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler


class SaxParserHandler(val itemsList: MutableList<BashItem>) : DefaultHandler() {
    private val TAG_ITEM = "item"
    private val TAG_GUID = "guid"
    private val TAG_TITLE = "title"
    private val TAG_DESCRIPTION = "description"
    private val TAG_PUB_DATE = "pubDate"
    private val TAG_LINK = "link"
    private val TAG_AUTHOR = "author"
    private val HTTP = "http"
    private val DESCRIPTION_CONTAINS = "<img src=\""

    private var mBashItem: BashItem = BashItem()
    private var isItemOpen = false
    private var isDescriptionOpen = false
    private var isGuidOpen = false
    private var isTitleOpen = false
    private var isPubDateOpen = false
    private var isLinkOpen = false
    private var isAuthorOpen = false
    private var mStringBuilder: StringBuilder = StringBuilder()
    private val mItems: MutableList<BashItem> by lazy { itemsList }

    override fun startElement(uri: String?, localName: String?,
                              qName: String?, attributes: Attributes?) {
        super.startElement(uri, localName, qName, attributes)
        if (TAG_ITEM == qName) {
            isItemOpen = true
        } else if (isItemOpen) {
            when (qName) {
                TAG_DESCRIPTION -> isDescriptionOpen = true
                TAG_AUTHOR -> isAuthorOpen = true
                TAG_GUID -> isGuidOpen = true
                TAG_LINK -> isLinkOpen = true
                TAG_PUB_DATE -> isPubDateOpen = true
                TAG_TITLE -> isTitleOpen = true
            }
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        super.endElement(uri, localName, qName)
        if (isItemOpen) {
            when (qName) {
                TAG_GUID -> isGuidOpen = false
                TAG_LINK -> {
                    mBashItem.link = mStringBuilder.toString()
                    isLinkOpen = false
                }
                TAG_TITLE -> {
                    mBashItem.title = mStringBuilder.toString()
                    isTitleOpen = false
                }
                TAG_PUB_DATE -> {
                    mBashItem.pubDate = DateUtils.parseDateFromString(mStringBuilder.toString())
                    isPubDateOpen = false
                }
                TAG_DESCRIPTION -> {
                    val description = mStringBuilder.toString()
                    if (description.contains(DESCRIPTION_CONTAINS)) {
                        mBashItem.description = description.substring(description.indexOf(HTTP),
                                description.length - 2)
                    } else {
                        mBashItem.description = description
                    }
                    isDescriptionOpen = false
                }
                TAG_AUTHOR -> {
                    val author = mStringBuilder.toString()
                    mBashItem.author = if(author.isEmpty()) null else author
                    isAuthorOpen = false
                }
                else -> {
                    mItems.add(mBashItem)
                    isItemOpen = false
                    mBashItem = BashItem()
                }
            }
            mStringBuilder = StringBuilder()
        }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        super.characters(ch, start, length)
        if (isItemOpen && (isAuthorOpen || isDescriptionOpen || isGuidOpen || isLinkOpen ||
                isPubDateOpen || isTitleOpen)) {
            for (index in start..start + length - 1) {
                mStringBuilder.append(ch!![index])
            }
        }
    }
}