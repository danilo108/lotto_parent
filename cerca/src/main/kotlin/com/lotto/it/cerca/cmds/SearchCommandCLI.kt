package com.lotto.it.cerca.cmds

import it.lotto.fileparser.parseCsvFile
import it.lotto.search.EstrazioniExtractor
import it.lotto.search.EstrazioniSearcher
import it.lotto.search.dtos.SearchResult
import it.lotto.search.dtos.SingleLineEstrazione
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import reactor.core.publisher.Flux
import java.io.File


@ShellComponent
class SearchCommandCLI{

    var singleLineEstrazioneFlux:Flux<SingleLineEstrazione>? = null
    var numbersToSearch:List<Int>? = null
    var matches:Int? = null


    private fun askHowManyCombinations(): Int {
        println("Quante combinazioni vuoi trovare in ogni estrazione? 2 per ambi, 3 per terne, etc ")
        val matches = try {
            readln()!!.toInt()
        } catch (e: Exception) {
            println("C'e' stato un errore per colpa tua!!! Devi scrivere un numerooooo ${e.message}")
            askHowManyCombinations()
        }
        return matches
    }


    private fun askWhichNumberToSearch(): List<Int> {
        println("Inserisci i numeri da cercare separati dalla virgola ")
        val numbersToSearch = try {
            readln()!!.split(",").map { it.toInt() }.toList()
        } catch (e: Exception) {
            println("C'e' stato un errore per colpa tua!!! Devi scrivere solo numeri separati dalla virgola.  ${e.message}")
            println("Per esempio: 1,2,3")
            askWhichNumberToSearch()
        }
        return numbersToSearch
    }

    private fun askWhichArchiveToLoad(): Flux<SingleLineEstrazione> {
        val archiveFilePath = selectArchive()
        val singleLineEstrazioneFlux =
            EstrazioniExtractor().estraiSingleLineEstrazioni(parseCsvFile(archiveFilePath))
        return singleLineEstrazioneFlux
    }

    private fun selectArchive(): String {
        val archiveFiles = askWhichArchiveFolder()
        val fileNumber = askWhichArchiveFile(archiveFiles)
        val archiveFilePath = archiveFiles[fileNumber]
        return archiveFilePath
    }

    @ShellMethod("inizia il programma", key = arrayOf("vai", "inizia", "daje", "allora", "namo"))
    fun inizia(){
        loadArchive()
        setNumbersToSearch()
        setHowManyCombinations()
        val result = search()
        println("Questo e' il risultato")
        println("$result")
    }

    @ShellMethod("cerca", key = arrayOf("cerca", "trova", "search"))
    fun cerca(){
        println("Questo e' il risultato della ricerca: \n${search()}")
    }

    @ShellMethod("Carica un altro archivio", key = arrayOf("carica", "ricarica","change-archive", "cambia-archivio", "ca" ))
    fun changeArchive(){
        resetArchive()
        loadArchive()
    }

    private fun resetArchive() {
        singleLineEstrazioneFlux = null
    }

    private fun search(): SearchResult {
        val archive = getFlux()
        val numbers = getNumberToSearch()
        val desiredMatches = getMatches()
        val result = EstrazioniSearcher().searchSingleCombination(numbers,
            desiredMatches, archive)
        return result
    }

    private fun getMatches(): Int {
        if(matches == null)
            setHowManyCombinations()
        return matches!!
    }

    private fun setHowManyCombinations():Int {
        matches = askHowManyCombinations()
        return matches!!
    }

    private fun setNumbersToSearch() {
        numbersToSearch = askWhichNumberToSearch()
    }

    private fun getNumberToSearch(): List<Int> {
        if(numbersToSearch == null)
            setNumbersToSearch()
        return numbersToSearch!!
    }

    private fun loadArchive() {
        singleLineEstrazioneFlux = askWhichArchiveToLoad()
    }

    private fun getFlux(): Flux<SingleLineEstrazione> {
        if(singleLineEstrazioneFlux == null)
            loadArchive()
        return singleLineEstrazioneFlux!!
    }


    private fun askWhichArchiveFile(archiveFiles: List<String>): Int {
        println("Scegli quale file sara' il tuo archivio: ")
        val fileNumber = try {
            val value = readln()!!.toInt()
            if(value < 0 || value > archiveFiles.size - 1) throw Exception("Daje un po'!!! Devi scegliere il numero di file!!! Da 0 a ${archiveFiles.size - 1}")
            value
        } catch (e: Exception) {
            println("C'e' stato un errore per colpa tua.\n ${e.message}")
            askWhichArchiveFile(archiveFiles)
        }
        println("Hai scelto ${archiveFiles[fileNumber]}... speriamo bene...")
        return fileNumber
    }

    private fun askWhichArchiveFolder(): List<String> {
        println("In quale cartella sono gli archivi? Usa . per la cartella corrente")
        val archiveDirectory = readln()!!
        println("Hai scelto:\n$archiveDirectory")
        val archiveFiles = listFiles(archiveDirectory)
        return archiveFiles
    }

    private fun listFiles(archiveDirectory: String): List<String> {
        try {
            val file = File(archiveDirectory)
            if(!file.exists()) throw Exception("Ma sta cartella nun esiste!!! Guarda come copiare il percorso di una cartella a questo sito\nhttps://versione-completa.it/blog/windows-copiare-visualizzare-percorso-file\n Se hai ancora problemi, chiama Massimo")
            if(!file.isDirectory) throw Exception("Ma questo e' un file non una cartella. Devi da seleziona' la cartella!!! Guarda come copiare il percorso di una cartella a questo sito\nhttps://versione-completa.it/blog/windows-copiare-visualizzare-percorso-file\n Se hai ancora problemi, chiama Massimo")
            val archiveFiles = file.walk().toList().map { it.absolutePath }
                .also { it.forEachIndexed { index, s -> println("$index) $s") } }
            return archiveFiles
        } catch (e: Exception) {
            println("C'e' stato un errore per colpa tua. Forse non hai inserito il percorso giusto per la cartella dell'archivio. \nUsa \".\" per indicare la cartella corrente. L'errore per colpa tua era:\n${e.message}")
            return askWhichArchiveFolder()
        }
    }
}