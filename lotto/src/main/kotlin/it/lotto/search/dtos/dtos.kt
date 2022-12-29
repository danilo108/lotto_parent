package it.lotto.search.dtos

import java.time.Duration
import java.time.LocalDate

data class Estrazione(val date: LocalDate, val ruote:List<Ruota> )
data class Ruota(val name:String, val numeri:List<Int>)
data class SingleLineEstrazione(val date: LocalDate, val numeri: List<Int>)
data class SearchResult(val numeri: List<Int>, val ritardoAttuale:Long, val ritardoStorico:Long, val frequenza:Int)
data class MatchingNumbers(private val numbers:List<Int>, private val matches:MutableList<SingleMatch> = mutableListOf()){
    val singleMatches get() = matches.toList()
    fun addDate(date: LocalDate, numberOfMatches: Int){
//        println("Adding matching $numberOfMatches matching for $date")
        val lastMatch = matches.lastOrNull()?.date?:date
//        println("lastMatch $lastMatch")
        val singleMatch =
            SingleMatch(date, numberOfMatches, Duration.between(lastMatch.atStartOfDay(), date.atStartOfDay()).toDays())
//        println("Single Match: $singleMatch")
        matches.add(singleMatch)}

}
data class SingleMatch(val date: LocalDate, val numberOfMatches:Int, val lastRitardo:Long)