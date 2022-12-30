package it.lotto.fileparser

import it.lotto.search.EstrazioniExtractor
import it.lotto.search.EstrazioniSearcher
import it.lotto.search.dtos.Estrazione
import it.lotto.search.dtos.Ruota
import it.lotto.search.dtos.SearchResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.io.BufferedWriter
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
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

    @Test
    fun test2022() {
        val file = createTempFile()
        write2022(file)
        val flux = parseCsvFile(file.toAbsolutePath().toString())
        val expectedRitardo =
            Duration.between(LocalDate.of(2022, 12, 20).atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()
        assertThat(EstrazioniSearcher().searchSingleCombination(listOf(5, 7, 11),
            3,
            EstrazioniExtractor().estraiSingleLineEstrazioni(flux)))
            .isEqualTo(SearchResult(listOf(5, 7, 11), expectedRitardo, expectedRitardo, 3))
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

    private fun write2022(file: Path) {
        BufferedWriter(FileWriter(file.toFile())).use { writer ->
            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02\n")
            IntRange(1, 1000).forEach {
                writer.write("${it+1000}-12-28\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\t90\n")
            }
            writer.write("2022-11-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02\n")
            writer.write("2022-11-21\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02\n")
//            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02")
//            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02")
//            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02")
//            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02")
//            writer.write("2022-12-20\t11\t25\t64\t06\t04\t27\t25\t54\t67\t84\t89\t66\t05\t11\t74\t78\t27\t45\t69\t11\t45\t07\t13\t25\t71\t46\t23\t29\t81\t87\t86\t02\t10\t51\t62\t34\t24\t25\t41\t13\t60\t85\t12\t11\t28\t39\t03\t15\t42\t31\t12\t38\t40\t56\t02")

        }
    }

}