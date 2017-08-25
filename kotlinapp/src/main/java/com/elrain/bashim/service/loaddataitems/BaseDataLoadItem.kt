package com.elrain.bashim.service.loaddataitems

interface BaseDataLoadItem {
    fun getUrl() : String
    fun getUserStringId() : Int
}