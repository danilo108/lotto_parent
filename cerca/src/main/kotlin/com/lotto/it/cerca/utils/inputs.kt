package com.lotto.it.cerca.utils

import org.jline.reader.LineReader
import org.springframework.util.StringUtils


class InputReader @JvmOverloads constructor(private val lineReader: LineReader, mask: Char? = null) {
    private val mask: Char

    init {
        this.mask = mask ?: DEFAULT_MASK
    }

    @JvmOverloads
    fun prompt(prompt: String, defaultValue: String? = null, echo: Boolean = true): String? {
        var answer: String? = ""
        answer = if (echo) {
            lineReader.readLine("$prompt: ")
        } else {
            lineReader.readLine("$prompt: ", mask)
        }
        return if (StringUtils.isEmpty(answer)) {
            defaultValue
        } else answer
    }

    companion object {
        const val DEFAULT_MASK = '*'
    }
}