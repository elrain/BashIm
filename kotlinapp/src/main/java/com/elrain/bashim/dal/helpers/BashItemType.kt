package com.elrain.bashim.dal.helpers

enum class BashItemType(id: Int) {
    QUOTE(1), COMICS(2), OTHER(3);

    private val mId: Int = id

    fun getId(): Int = mId

    companion object {
        fun getTypeById(id: Int): BashItemType {
            var retval : BashItemType = QUOTE
            when(id){
                2 -> retval = COMICS
                3 -> retval = OTHER
            }
            return retval
        }
    }
}