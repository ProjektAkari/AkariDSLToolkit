import pw.akari.dsltk.lex.Lexer
import pw.akari.dsltk.parse.*
import kotlin.test.Test

class Parse {
    @Test
    fun testParser() {
        val source = """
            apply effect poison amplifier 2 duration 10 to player Maple
            send a to console
            """.trimIndent()
        val tokens = Lexer.lex(source)
        println(tokens.content)
        val ast = Parser(tokens, listOf(
            makeActionRule("apply_effect") {
                keyword("apply")
                keyword("effect")
                argument("effect_type")
                keyword("amplifier")
                argument("amplifier")
                keyword("duration")
                argument("duration")
                keyword("to")
                keyword("player")
                argument("target")
            },
            makeActionRule("send") {
                keyword("send")
                argument("message")
                keyword("to")
                argument("target")
            }
        )).parse()
        ASTPrettyPrinter.print(ast)
    }

}