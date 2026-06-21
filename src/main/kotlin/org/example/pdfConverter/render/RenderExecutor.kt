package org.example.pdfConverter.render

import java.util.concurrent.Executors

class RenderExecutor : AutoCloseable {
    private val executor = Executors.newSingleThreadExecutor()

    fun submit(task: () -> Unit) {
        executor.submit(task)
    }

    override fun close() {
        executor.shutdown()
    }
}
