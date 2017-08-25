package com.elrain.bashim.service.loaddataitems

import com.elrain.bashim.R

class CommicsDataLoadItem : BaseDataLoadItem {
    override fun getUrl(): String = "http://bash.im/rss/comics.xml"
    override fun getUserStringId(): Int = R.string.splash_commics_downloaded
}