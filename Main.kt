package flashcards

const val ASK_ACTION = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"
const val BYE = "Bye bye!"

fun main(args: Array<String>) {
    var curResponse: String
    val dictionary = DeckOfFlashCards()
    if (args.contains("-import")) dictionary.importCards { args[args.indexOf("-import") + 1] }
    while (true) {
        dictionary.printlnAndSaveToLog(ASK_ACTION)
        curResponse = dictionary.readlnAndSaveToLog()
        when (curResponse) {
            "add" -> dictionary.addCard()
            "remove" -> dictionary.removeCard()
            "import" -> dictionary.importCards()
            "export" -> dictionary.exportCards()
            "ask" -> dictionary.askSomeCards()
            "log" -> dictionary.saveLogAt()
            "hardest card" -> dictionary.askHardestCard()
            "reset stats" -> dictionary.resetStats()
            else -> break
        }
        dictionary.printlnAndSaveToLog()
    }
    println(BYE)
    if (args.contains("-export")) dictionary.exportCards { args[args.indexOf("-export") + 1] }
}
