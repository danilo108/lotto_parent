package it.lotto.search

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SearchAppTest{


    @Test
    fun runIt(){
        SearchApp().main(listOf<String>().toTypedArray())
    }
}