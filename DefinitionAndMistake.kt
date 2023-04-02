package flashcards

data class DefinitionAndMistake(
    val definition: String,
    var mistakes: Int = 0
)
