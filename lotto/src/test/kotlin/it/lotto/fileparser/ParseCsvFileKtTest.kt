package it.lotto.fileparser

import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.Ruota
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.random.Random

internal class ParseCsvFileKtTest {


    @Test
    fun skipLines() {

        val file = createTempFile()
        writeToFile(file)
        val flux = parseCsvFile(file.toAbsolutePath().toString())
        StepVerifier.create(flux)
            .expectNext(
                Estrazione(date = LocalDate.of(2021, 12, 28),
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
            )
            .verifyComplete()


    }

    private fun createTempFile(): Path {
        return Files.createTempFile("temp-${Random.nextInt()}", ".csv")
    }

    private fun writeToFile(file: Path) {
        BufferedWriter(FileWriter(file.toFile())).use { writer ->
            writer.write("24 Dicembre 2022, Sabato																																																							\n")
            writer.write("Gioco del Lotto Archivio estrazioni e statistiche (2021)																																																							\n")
            writer.write("																																																							\n")
            writer.write("DATE	Bari					Cagliari					Firenze					Genova					Milano					Napoli					Palermo					Roma					Torino					Venezia					Nazionale				\n")
            writer.write("2021-12-28\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\n")
            writer.write("another wrong line\n")

        }
    }

}