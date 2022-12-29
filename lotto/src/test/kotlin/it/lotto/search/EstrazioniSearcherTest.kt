package it.lotto.search

import it.lotto.search.dtos.MatchingNumbers
import it.lotto.search.dtos.SearchResult
import it.lotto.search.dtos.SingleLineEstrazione
import it.lotto.search.dtos.SingleMatch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context
import reactor.util.context.ContextView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime


internal class EstrazioniSearcherTest {


    @Test
    fun findOne() {
        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf(1, 2, 3), 3,
                    Flux.just(
                        createOrderedSingleLineEstrazione(LocalDate.now().minusDays(2)),
                        createOrderedSingleLineEstrazione(LocalDate.now().minusDays(1)),
                    )
                )
        ).isEqualTo(SearchResult(numeri = listOf(1, 2, 3), ritardoAttuale = 1, ritardoStorico = 1, frequenza = 2))
    }
    @Test
    fun findSome() {
        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf( 2, 3), 2,
                    Flux.just(
                        SingleLineEstrazione(LocalDate.now().minusDays(10), listOf(1,2,3,4,5)),
                        SingleLineEstrazione(LocalDate.now().minusDays(7), listOf(2,3,4,6,7)),
                        SingleLineEstrazione(LocalDate.now().minusDays(2), listOf(2,3,6,7,8)),
                        SingleLineEstrazione(LocalDate.now().minusDays(2), listOf(6,7,8,9)),
                    )
                )
        ).isEqualTo(SearchResult(numeri = listOf(2, 3), ritardoAttuale = 2, ritardoStorico = 5, frequenza = 3))
    }
    @Test
    fun findNone() {
        val daysSinceMIN = Duration.between(LocalDateTime.MIN, LocalDateTime.now()).toDays()
        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf( 3,4,5,6,7,8), 5,
                    Flux.just(
                        SingleLineEstrazione(LocalDate.now().minusDays(10), listOf(1,2,3,4,5)),
                        SingleLineEstrazione(LocalDate.now().minusDays(7), listOf(2,3,4,6,7)),
                        SingleLineEstrazione(LocalDate.now().minusDays(2), listOf(2,3,6,7,8)),
                        SingleLineEstrazione(LocalDate.now().minusDays(2), listOf(6,7,8,9)),
                    )
                )
        ).isEqualTo(SearchResult(numeri = listOf(3,4,5,6,7,8), ritardoAttuale = daysSinceMIN, ritardoStorico = daysSinceMIN, frequenza = 0))
    }
    @Test
    fun findMultipleInTheSameEstrazione() {

        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf( 3,4,5,), 2,
                    Flux.just(
                        SingleLineEstrazione(LocalDate.now().minusDays(10), listOf(1,2,3,5)),
                        SingleLineEstrazione(LocalDate.now().minusDays(7), listOf(2,3,4,5,7)),
                        SingleLineEstrazione(LocalDate.now().minusDays(2), listOf(2,3,5,6,7,8)),
                        SingleLineEstrazione(LocalDate.now().minusDays(1), listOf(4,5,6,7,8,9)),
                    )
                )
        ).isEqualTo(SearchResult(numeri = listOf(3,4,5), ritardoAttuale = 1, ritardoStorico = 5, frequenza = 4))
    }


    @Test
    fun find1000() {
        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf(1, 2, 3), 3,
                    Flux.fromIterable(LongRange(10, 1009).reversed().map { createOrderedSingleLineEstrazione(LocalDate.now().minusDays(it)) })

                )
        ).isEqualTo(SearchResult(numeri = listOf(1, 2, 3), ritardoAttuale = 10, ritardoStorico = 10, frequenza = 1000))
    }

    @Test
    fun find100000() {
        assertThat(
            EstrazioniSearcher()
                .searchSingleCombination(listOf(1, 2, 3), 3,
                    Flux.fromIterable(LongRange(10, 1000009).reversed().map { createOrderedSingleLineEstrazione(LocalDate.now().minusDays(it)) })
                )
        ).isEqualTo(SearchResult(numeri = listOf(1, 2, 3), ritardoAttuale = 10, ritardoStorico = 10, frequenza = 1000000))
    }


    @Test
    fun fluxTest() {
        val key = "message"
        val r = Mono.just("Hello")
            .flatMap { s: String ->
                Mono.deferContextual { ctx: ContextView ->
                    Mono.just(s + " " + ctx.get(key))
                }
            }
            .contextWrite { ctx: Context ->
                ctx.put(key,
                    "World")
            }

        StepVerifier.create(r)
            .expectNext("Hello World")
            .verifyComplete()
    }

    @Test
    fun fluxTest2() {
        val key = "message"
        val f = Flux.just("Hello")
            .flatMap { s: String ->
                Flux.deferContextual { ctx: ContextView -> Flux.just(s + " " + ctx.get(key)) }
            }.contextWrite { ctx: Context ->
                ctx.put(key,
                    "World")
            }
        StepVerifier.create(f)
            .expectNext("Hello World")
            .verifyComplete()
    }

    @Test
    fun calculateRitardoAttuale() {
        assertThat(EstrazioniSearcher().findRitardoAttuale(MatchingNumbers(numbers = listOf(1, 2, 3),
            matches = mutableListOf(
                SingleMatch(LocalDate.now().minusDays(10), 2, lastRitardo = 3)
            )), 2))
            .isEqualTo(10)

    }
    @Test
    fun calculateRitardoStorico() {
        assertThat(EstrazioniSearcher().findRitardoStorico(MatchingNumbers(numbers = listOf(1, 2, 3),
            matches = mutableListOf(
                SingleMatch(LocalDate.now().minusDays(60), 2, lastRitardo = 2),
                SingleMatch(LocalDate.now().minusDays(10), 2, lastRitardo = 50),
                SingleMatch(LocalDate.now().minusDays(7), 2, lastRitardo = 3),
            )), 2))
            .isEqualTo(50)

    }


    @Test
    fun calculateFrequenza() {
        assertThat(EstrazioniSearcher().findFrequenza(MatchingNumbers(numbers = listOf(1, 2, 3),
            matches = mutableListOf(
                SingleMatch(LocalDate.now().minusDays(10), 2, 9999),
                SingleMatch(LocalDate.now().minusDays(10), 2, 9999),
                SingleMatch(LocalDate.now().minusDays(10), 3, 9999),
                SingleMatch(LocalDate.now().minusDays(10), 2, 9999),
            )), 2))
            .isEqualTo(4)

    }

}