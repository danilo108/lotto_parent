package it.lotto.fileparser

import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.Ruota
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import reactor.core.publisher.Flux
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Ruotas {
    BARI,CAGLIARI,FIRENZE,GENOVA,MILANO,NAPOLI,PALERMO,ROMA,TORINO,VENEZIA,NAZIONALE,
}


//download files from file:///Users/daniloscuderoni/Downloads/it-lotto-past-draws-archive%20(1).xls
fun parseCsvFile(filePath: String): Flux<Estrazione> {
    return Flux.using({
        val inputStream = FileInputStream(filePath)
        val reader = InputStreamReader(inputStream)
        CSVParser(reader,  CSVFormat.MONGODB_TSV)
    }, { parser ->
        Flux.fromIterable(parser.records)
            .filter { record ->
                try {
                    SimpleDateFormat("yyyy-MM-dd").parse(record[0])
                    true
                } catch (e: Exception) {
                    false
                }
            }
            .map { record ->
                val date = LocalDate.parse(record[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val ruote = Ruotas.values().zip(record.drop(1).chunked(5)) { name, numeri ->
                    Ruota(name.name, numeri.map { it.toInt() })
                }
                Estrazione(date, ruote)
            }
    }, { parser ->
        parser.close()
    })
}