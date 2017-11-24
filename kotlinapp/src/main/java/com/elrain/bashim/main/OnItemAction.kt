package com.elrain.bashim.main

import com.elrain.bashim.entities.BashItem

interface OnItemAction {
    fun openInTab(url: String)
    fun shareItem(item: BashItem)
}