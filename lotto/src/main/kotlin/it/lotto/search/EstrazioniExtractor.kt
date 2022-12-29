package it.lotto.search

import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.SingleLineEstrazione
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class EstrazioniExtractor() {
    fun estraiSingleLineEstrazioni(estrazioni:Flux<Estrazione>):Flux<SingleLineEstrazione>{
     return estrazioni.flatMap { Mono.just(SingleLineEstrazione(it.date, numeri = it.ruote.flatMap { it.numeri }.toSet().sorted())) }
    }
}