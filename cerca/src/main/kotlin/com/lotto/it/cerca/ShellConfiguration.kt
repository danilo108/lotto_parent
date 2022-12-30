package com.lotto.it.cerca

import com.lotto.it.cerca.utils.InputReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ShellConfiguration {
    @Bean
    fun inputReader(@org.springframework.context.annotation.Lazy lineReader: org.jline.reader.LineReader): InputReader {
        return InputReader(lineReader)
    }
}