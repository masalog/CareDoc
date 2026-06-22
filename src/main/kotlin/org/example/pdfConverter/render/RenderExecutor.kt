package org.example.pdfConverter.render

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RenderExecutor : AutoCloseable {
    private val executor = Executors.newSingleThreadExecutor()

    fun submit(task: () -> Unit) {
        executor.submit(task)
    }

    override fun close() {
        executor.shutdownNow()
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}
