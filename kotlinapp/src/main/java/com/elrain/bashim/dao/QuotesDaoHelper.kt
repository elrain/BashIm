package com.elrain.bashim.dao

import com.elrain.bashim.AppDatabase
import com.elrain.bashim.BashItemType
import com.elrain.bashim.entities.BashItem
import com.elrain.bashim.entities.TempEntity

class QuotesDaoHelper(val appDb: AppDatabase) {

        fun deleteOldAndSaveNewTempQuotes(bashItem: List<BashItem>) {
            val quotesDao = appDb.quotesDao()
            val tempDao = appDb.tempDao()

            quotesDao.deleteOtherItems()
            tempDao.deleteOldRows()
            bashItem.forEach {
                val insertedId = quotesDao.saveQuote(it)
                tempDao.insertRef(TempEntity(insertedId))
            }
        }

        fun getItemsByType(type: BashItemType): List<BashItem> {
            return when (type) {
                BashItemType.COMICS -> appDb.quotesDao().getCommics()
                BashItemType.OTHER -> appDb.quotesDao().getTempItems()
                else -> appDb.quotesDao().getQuotes()
            }
        }
}