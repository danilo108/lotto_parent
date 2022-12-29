package it.lotto.search

import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.Ruota
import it.lotto.search.dtos.SingleLineEstrazione
import reactor.core.publisher.Flux
import java.time.LocalDate


 fun generateSingleLineEstrazioni(numbOfExtractions: Int) =
    IntRange(0, numbOfExtractions).map { createOrderedSingleLineEstrazione(LocalDate.now()) }

 fun extractEstrazioni(numbOfExtractions: Int) = EstrazioniExtractor()
    .estraiSingleLineEstrazioni(
        Flux.fromIterable(IntRange(0, numbOfExtractions).map { createOrderedEstrazione(LocalDate.now()) }
        )
    )


 fun createOrderedSingleLineEstrazione(date: LocalDate)= SingleLineEstrazione(date, IntRange(1, 110).toList())

 fun createOrderedEstrazione(date: LocalDate): Estrazione {
    return Estrazione(date, ruote = listOf(
        createOrderedRuota("BARI", 1),
        createOrderedRuota("CAGLIARI", 11),
        createOrderedRuota("FIRENZE", 21),
        createOrderedRuota("GENOVA", 31),
        createOrderedRuota("MILANO", 41),
        createOrderedRuota("NAPOLI", 51),
        createOrderedRuota("PALERMO", 61),
        createOrderedRuota("ROMA", 71),
        createOrderedRuota("TORINO", 81),
        createOrderedRuota("VENEZIA", 91),
        createOrderedRuota("NAZIONALE", 101),
    ))
}

private fun createOrderedRuota(ruota: String, seed: Int) = Ruota(ruota, IntRange(seed, seed + 9).toList())