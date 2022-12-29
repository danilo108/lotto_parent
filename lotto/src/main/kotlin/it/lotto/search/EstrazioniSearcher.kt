package it.lotto.search

import it.lotto.search.dtos.MatchingNumbers
import it.lotto.search.dtos.SearchResult
import it.lotto.search.dtos.SingleLineEstrazione
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class EstrazioniSearcher {

    fun searchSingleCombination(
        numbers: List<Int>,
        desiredMatches: Int,
        archive: Flux<SingleLineEstrazione>,
    ): SearchResult {
        val matchings = MatchingNumbers(numbers)
        val numbersToFind = numbers.toList()
        val matches = desiredMatches
        Flux.from(archive)
//            .contextWrite { ctx ->
//                ctx.put("desiredMatches", desiredMatches)
//                    .put("matchings", matchings)
//                    .put("numbers", numbers)
//            }
            .filter { estrazione ->

                val found = estrazione.numeri.size - (estrazione.numeri - numbersToFind).size
//                println("found $found")
                found >= matches
            }
            .doOnNext {estrazione ->
                matchings.addDate(estrazione.date, estrazione.numeri.size - (estrazione.numeri - numbersToFind).size )
            }
            .toIterable().toList()

        return SearchResult(numeri = numbers,
            findRitardoAttuale(matchings, desiredMatches),
            findRitardoStorico(matchings, desiredMatches),
            findFrequenza(matchings, desiredMatches))
    }

    internal fun findFrequenza(matchings: MatchingNumbers, matches: Int): Int {
        return matchings.singleMatches.filter { it.numberOfMatches >= matches }.count()
    }

    internal fun findRitardoAttuale(matchings: MatchingNumbers, matches: Int): Long {
        val lastDate = findLastDate(matchings, matches)
        return calculateDaysSince(lastDate)
    }

    private fun calculateDaysSince(lastDate: LocalDate) =
        Duration.between(lastDate.atStartOfDay(), LocalDateTime.now()).toDays()

    private fun findLastDate(matchings: MatchingNumbers, matches: Int): LocalDate {
        val lastDate = matchings.singleMatches.findLast { it.numberOfMatches >= matches }?.date
            ?: matchings.singleMatches.firstOrNull()?.date ?: LocalDate.MIN
        return lastDate
    }

    internal fun findRitardoStorico(matchings: MatchingNumbers, matches: Int): Long {
        val ritardoAttuale = findRitardoAttuale(matchings, matches)
        val ritardoStorico = matchings.singleMatches.maxOfOrNull { it.lastRitardo }
        return maxOf(ritardoStorico?:0, ritardoAttuale)
    }

}