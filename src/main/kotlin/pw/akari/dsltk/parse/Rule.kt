package pw.akari.dsltk.parse

sealed interface Rule {
    val elements: List<Element>
}

enum class ElementType {
    KEYWORD, ARGUMENT;
}

data class Element(val type: ElementType, val text: String)

class ActionRule(val name: String, override val elements: List<Element>) : Rule

class ArgumentRule(override val elements: List<Element>) : Rule

class ExprActionRule(override val elements: List<Element>) : Rule
