package com.elrain.bashim.dao

import java.util.*

data class BashItem(var id: Long, var title: String, var description: String, var link: String,
                    var pubDate: Date, var author: String?) {
    constructor() : this(0L, "", "", "", Date(), null)
}