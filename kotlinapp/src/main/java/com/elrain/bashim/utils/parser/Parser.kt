package com.elrain.bashim.utils.parser

import com.elrain.bashim.entities.BashItem

interface Parser {

    fun parse(): List<BashItem>
}
