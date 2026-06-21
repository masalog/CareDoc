package org.example.pdfConverter.render

class RenderJobManager {
    @Volatile
    private var seq: Long = 0

    fun nextSeq(): Long = ++seq

    fun isLatest(jobSeq: Long): Boolean = jobSeq == seq
}