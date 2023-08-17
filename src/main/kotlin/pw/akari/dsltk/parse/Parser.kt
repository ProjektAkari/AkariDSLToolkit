package pw.akari.dsltk.parse

import pw.akari.dsltk.lex.TokenStream
import pw.akari.dsltk.lex.TokenStream.Companion.test
import pw.akari.dsltk.lex.TokenStream.Companion.testAndSkip
import pw.akari.dsltk.lex.TokenType
import pw.akari.dsltk.lex.TokenType.*

/**
 * Backtracking Parser
 *
 * 回溯式语法分析器
 *
 * 每个子函数在分析失败时应当回溯并返回null
 */
class Parser(val source: TokenStream, val ruleSet: List<ActionRule>) {
    fun parse(): Node {
        val actions = mutableListOf<ActionNode>()
        while(!source.test(EOF)) {
            parseAction()?.let { actions.add(it) }
        }
        return BlockNode(actions)
    }

    fun parseAction(): ActionNode? {
        for (rule in ruleSet) {
            matchActionRule(rule)?.let { return it }
        }
        return null
    }

    fun matchActionRule(rule: ActionRule): ActionNode? {
        val offset = source.offset
        val args = mutableMapOf<String, ExprNode>()
        for (element in rule.elements) {
            when(element.type) {
                ElementType.KEYWORD -> {
                    if(!source.testAndSkip(element.text)) {
                        source.reset(offset)
                        return null
                    }
                }
                ElementType.ARGUMENT -> {
                    val expr = parseExpr()
                    if(expr == null) {
                        source.reset(offset)
                        return null
                    }
                    args[element.text] = expr
                }
            }
        }
        return ActionNode(rule.name, args)
    }

    fun parseExpr(): ExprNode? {
        val offset = source.offset
        return when(source.peek().type) {
            WORD, STRING -> StringNode(source.next().text)
            VAR_ID -> VarAccessNode(source.next().text)
            NUMBER -> NumberNode(source.next().text.toDouble())
            KEY_TRUE -> {
                source.skip()
                BooleanNode.TRUE
            }
            KEY_FALSE -> {
                source.skip()
                BooleanNode.FALSE
            }
            else -> {
                source.reset(offset)
                null
            }
        }
    }
}