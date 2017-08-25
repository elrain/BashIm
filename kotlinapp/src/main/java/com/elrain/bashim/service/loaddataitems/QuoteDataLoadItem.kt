package com.elrain.bashim.service.loaddataitems

import com.elrain.bashim.R

class QuoteDataLoadItem : BaseDataLoadItem {
    override fun getUrl(): String = "http://bash.im/rss"
    override fun getUserStringId(): Int = R.string.splash_quotes_downloaded
}