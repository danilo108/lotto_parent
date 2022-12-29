package it.lotto.search

import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.Ruota
import it.lotto.search.dtos.SingleLineEstrazione
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal class EstrazioniExtractorTest {

    @Test
    fun singleLineSuccess() {
        assertThat(
            EstrazioniExtractor()
                .estraiSingleLineEstrazioni(
                    Flux.just(all90s())
                ).toIterable().toList()
        ).containsExactly(
            SingleLineEstrazione(LocalDate.of(2021, 12, 28), listOf(90))
        )

    }

    @Test
    fun reversed521Test() {
        assertThat(
            EstrazioniExtractor()
                .estraiSingleLineEstrazioni(
                    Flux.just(reversed521())
                ).toIterable().toList()
        ).containsExactly(
            SingleLineEstrazione(LocalDate.of(2021, 12, 28), listOf(1,2,3,4,5))
        )

    }

    @Test
    fun duplicatedNumbersInEstrazioni() {
        assertThat(
            EstrazioniExtractor()
                .estraiSingleLineEstrazioni(
                    Flux.just(createOrderedEstrazione(LocalDate.now())
                    )
                ).toIterable().toList()
        ).containsExactly(createOrderedSingleLineEstrazione(LocalDate.now()))

    }

    fun all90s()= Estrazione(date = LocalDate.of(2021, 12, 28),
        ruote = listOf(
            Ruota("BARI", IntRange(1, 5).map { 90 }.toList()),
            Ruota("CAGLIARI", IntRange(1, 5).map { 90 }.toList()),
            Ruota("FIRENZE", IntRange(1, 5).map { 90 }.toList()),
            Ruota("GENOVA", IntRange(1, 5).map { 90 }.toList()),
            Ruota("MILANO", IntRange(1, 5).map { 90 }.toList()),
            Ruota("NAPOLI", IntRange(1, 5).map { 90 }.toList()),
            Ruota("PALERMO", IntRange(1, 5).map { 90 }.toList()),
            Ruota("ROMA", IntRange(1, 5).map { 90 }.toList()),
            Ruota("TORINO", IntRange(1, 5).map { 90 }.toList()),
            Ruota("VENEZIA", IntRange(1, 5).map { 90 }.toList()),
            Ruota("NAZIONALE", IntRange(1, 5).map { 90 }.toList()),
        )
    )
    fun reversed521()= Estrazione(date = LocalDate.of(2021, 12, 28),
        ruote = listOf(
            Ruota("BARI", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("CAGLIARI", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("FIRENZE", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("GENOVA", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("MILANO", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("NAPOLI", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("PALERMO", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("ROMA", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("TORINO", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("VENEZIA", IntRange(1, 5).reversed().map { it }.toList()),
            Ruota("NAZIONALE", IntRange(1, 5).reversed().map { it }.toList()),
        )
    )


    @Test
    fun single1000EstrazioniSuccess() {
        val numbOfExtractions = 999
        assertThat(
            extractEstrazioni(numbOfExtractions).toIterable().toList()
        ).containsSequence(generateSingleLineEstrazioni(numbOfExtractions).toList())

    }


    @Test()
    fun single100000EstrazioniSuccess() {
        var output:Flux<SingleLineEstrazione>? = null
        val numbOfExtractions = 99999
        assertTimeout(Duration.of(2, ChronoUnit.MINUTES) , { output = extractEstrazioni(numbOfExtractions) })

        assertThat(
            output!!.toIterable().toList()
        ).containsSequence(generateSingleLineEstrazioni(numbOfExtractions).toList())

    }



}
/*
BARI	42	46	39	44	1
CAGLIARI	51	8	46	24	54
FIRENZE	67	30	82	8	32
GENOVA	62	28	46	8	67
MILANO	4	7	77	68	9
NAPOLI	12	80	69	2	67
PALERMO	70	50	71	52	26
ROMA	70	62	3	66	76
TORINO	8	56	20	85	86
VENEZIA	64	11	33	53	68
NAZIONALE
 */