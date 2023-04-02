package flashcards

import java.io.File

const val ASK_FILE_NAME = "File name:"
const val THE_CARD = "The card:"
const val THE_DEFINITION = "The definition of the card:"
const val WHICH_CARD = "Which card?"
const val THE_CARD_HAS_BEEN_REMOVED = "The card has been removed."
const val FILE_NOT_FOUND = "File not found."

class DeckOfFlashCards {
    private val dictionary = mutableMapOf<String, DefinitionAndMistake>()
    private var log: String = ""

    private fun isTermExist(dictionary: MutableMap<String, DefinitionAndMistake>, term: String): Boolean {
        return if (dictionary.containsKey(term)) {
            printlnAndSaveToLog("The card \"$term\" already exists. Try again:")
            true
        }
        else false
    }
    private fun isDefinitionExist(dictionary: MutableMap<String, DefinitionAndMistake>, definition: String): Boolean {
        return if (dictionary.any { it.value.definition == definition }) {
            printlnAndSaveToLog("The definition \"$definition\" already exists. Try again:")
            true
        }
        else false
    }

    private fun askFileName(): String {
        printlnAndSaveToLog(ASK_FILE_NAME)
        return readlnAndSaveToLog()
    }

    fun printlnAndSaveToLog(str: String = "") {
        println(str)
        log += "$str\n"
    }
    fun readlnAndSaveToLog(): String {
        val str = readln()
        log += "$str\n"
        return str
    }

    fun resetStats() {
        for (term in dictionary.keys) dictionary[term]?.mistakes = 0
        printlnAndSaveToLog("Card statistics have been reset.")
    }

    fun askHardestCard() {
        val listOfTermsWithMaxMistakes = mutableListOf<String>()
        var max = Int.MIN_VALUE
        for ((term, value) in dictionary) {
            if (value.mistakes > max) {
                listOfTermsWithMaxMistakes.clear()
                listOfTermsWithMaxMistakes.add(term)
                max = value.mistakes
            }
            else if (value.mistakes == max) listOfTermsWithMaxMistakes.add(term)
        }
        if (max == 0 || max == Int.MIN_VALUE) {
            printlnAndSaveToLog("There are no cards with errors.")
        }
        else if (listOfTermsWithMaxMistakes.size == 1) {
            printlnAndSaveToLog("The hardest card is \"${listOfTermsWithMaxMistakes[0]}\". You have $max errors answering it.")
        }
        else {
            var output = "The hardest cards are "
            for (word in listOfTermsWithMaxMistakes) output += "\"$word\", "
            printlnAndSaveToLog("${ output.substring(0 until output.lastIndex - 1) }. You have $max errors answering them.")
        }
    }

    fun saveLogAt() {
        printlnAndSaveToLog(ASK_FILE_NAME)
        val filename = readlnAndSaveToLog()
        val file = File(filename)
        file.writeText(log)
        printlnAndSaveToLog("The log has been saved.")
    }

    fun addCard() {
        printlnAndSaveToLog(THE_CARD)
        val term = readlnAndSaveToLog()
        if (isTermExist(dictionary, term)) return
        printlnAndSaveToLog(THE_DEFINITION)
        val definition = readlnAndSaveToLog()
        if (isDefinitionExist(dictionary, definition)) return
        dictionary[term] = DefinitionAndMistake(definition)
        printlnAndSaveToLog("The pair (\"$term\":\"$definition\") has been added.")
    }

    fun removeCard() {
        printlnAndSaveToLog(WHICH_CARD)
        val term = readlnAndSaveToLog()
        if (dictionary.containsKey(term)) {
            dictionary.remove(term)
            printlnAndSaveToLog(THE_CARD_HAS_BEEN_REMOVED)
        }
        else {
            printlnAndSaveToLog("Can't remove \"$term\": there is no such card.")
        }
    }

    fun importCards(getFileName: () -> String = ::askFileName) {
        val file = File(getFileName())
        if (!file.exists()) printlnAndSaveToLog(FILE_NOT_FOUND)
        else {
            var amount = 0
            file.forEachLine {
                val (t, d, m) = it.split(':')
                dictionary[t] = DefinitionAndMistake(d, m.toInt())
                amount++
            }
            printlnAndSaveToLog("$amount cards have been loaded.")
        }
    }

    fun exportCards(getFileName: () -> String = ::askFileName) {
        val file = File(getFileName())
        var toUpload = ""
        for (pair in dictionary) {
            toUpload += "${pair.key}:${pair.value.definition}:${pair.value.mistakes}\n"
        }
        file.writeText(toUpload)
        printlnAndSaveToLog("${dictionary.size} cards have been saved.")
    }

    fun askSomeCards() {
        printlnAndSaveToLog("How many times to ask?")
        val amount = readlnAndSaveToLog().toInt()
        var curCounter = 0
        for ((k, v) in dictionary) {
            if (curCounter++ >= amount) break
            printlnAndSaveToLog("Print the definition of \"$k\":")
            val answer = readlnAndSaveToLog()
            if (dictionary[k]?.definition == answer) printlnAndSaveToLog("Correct!")
            else if (dictionary.any { it.value.definition == answer }) {
                val keyByValue: String = dictionary.keys.first { dictionary[it]!!.definition == answer }
                dictionary[k]!!.mistakes++
                printlnAndSaveToLog("Wrong. The right answer is \"${v.definition}\", but your definition is correct for \"$keyByValue\".")
            }
            else {
                dictionary[k]!!.mistakes++
                printlnAndSaveToLog("Wrong. The right answer is \"${dictionary[k]!!.definition}\".")
            }
        }
    }
}