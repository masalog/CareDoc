package org.example.pdfConverter.render

import java.util.concurrent.atomic.AtomicLong

class RenderJobManager {

    private val seq = AtomicLong(0)

    fun nextSeq(): Long = seq.incrementAndGet()

    fun isLatest(jobSeq: Long): Boolean = jobSeq == seq.get()
}