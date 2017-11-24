package com.elrain.bashim

import com.elrain.bashim.entities.BashItem
import java.util.concurrent.Executors

class BackgroundRequestHandler {

    private val threadPoolExecutor = Executors.newSingleThreadExecutor()

    fun getList(request: () -> List<BashItem>, onResult: (List<BashItem>) -> Unit) {
        threadPoolExecutor.execute { onResult.invoke(request.invoke()) }
    }
}