package com.elrain.bashim.utils.parser

import com.elrain.bashim.dao.BashItem

interface Parser {

    fun parse(): List<BashItem>
}
