package com.elrain.bashim.utils.singletons

import android.content.Context
import android.content.SharedPreferences
import com.elrain.bashim.BashItemType

class BashImPreferences private constructor(context: Context){
    companion object : SingletonHolder<BashImPreferences, Context>(::BashImPreferences)

    private val LAST_SELECTED_TYPE = "lastSelectedType"
    private val sharedPreferences = context.getSharedPreferences("BashImPrefs.pref", Context.MODE_PRIVATE)

    fun saveLastSelectedType(type: BashItemType){
        sharedPreferences.edit().putInt(LAST_SELECTED_TYPE, type.ordinal).apply()
    }

    fun getLastSelectedType() : BashItemType{
        val type = sharedPreferences.getInt(LAST_SELECTED_TYPE, -1)
        return when(type){
            BashItemType.QUOTE.ordinal -> BashItemType.QUOTE
            BashItemType.COMICS.ordinal -> BashItemType.COMICS
            BashItemType.OTHER.ordinal -> BashItemType.OTHER
            else -> BashItemType.QUOTE
        }
    }
}