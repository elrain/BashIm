package com.elrain.bashim.service.runnablesfactory

import android.content.Context

class DownloadRunnableFactory(val context: Context) {

    fun getRunnable(type: DownloadRunnableTypes): BaseRunnable {
        if (type == DownloadRunnableTypes.MAIN) {
            return MainDownloadRunnable(context)
        } else if (type == DownloadRunnableTypes.OTHER) {
            return OtherDownloadRunnable(context)
        }

        throw IllegalArgumentException("Type ${type.name} cannot be processed with this factory")
    }

    enum class DownloadRunnableTypes {
        MAIN, OTHER
    }
}