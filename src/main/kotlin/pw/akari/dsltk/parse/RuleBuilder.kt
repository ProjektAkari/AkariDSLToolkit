package pw.akari.dsltk.parse

sealed class RuleBuilder<T : Rule> {
    val elements = mutableListOf<Element>()

    abstract fun build(): T

    fun keyword(word: String) {
        elements.add(Element(ElementType.KEYWORD, word))
    }

    fun argument(key: String) {
        elements.add(Element(ElementType.ARGUMENT, key))
    }
}

class ActionRuleBuilder(val name: String) : RuleBuilder<ActionRule>() {
    //val argumentClauses = mutableListOf<ArgumentRule>()

    override fun build(): ActionRule = ActionRule(name, elements)
/*
    fun argumentClause(rule: ArgumentRule) {
        argumentClauses.add(rule)
    }
    fun argumentClause(block: RuleBuilder<ArgumentRule>.() -> Unit) {
        argumentClauses.add(makeArgumentRule(block))
    }

 */
}

fun makeActionRule(name: String, block: ActionRuleBuilder.() -> Unit): ActionRule {
    val builder = ActionRuleBuilder(name)
    builder.block()
    return builder.build()
}

class ArgumentRuleBuilder : RuleBuilder<ArgumentRule>() {
    override fun build(): ArgumentRule = ArgumentRule(elements)
}

fun makeArgumentRule(block: ArgumentRuleBuilder.() -> Unit): ArgumentRule {
    val builder = ArgumentRuleBuilder()
    builder.block()
    return builder.build()
}
