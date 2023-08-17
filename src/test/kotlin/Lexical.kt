import pw.akari.dsltk.lex.Lexer
import kotlin.test.Test

class Lexical {
    @Test
    fun testLexer() {
        val source = """
            apply effect { type: 'poison', amplifier: 2, duration: 10 } to player 'Maple'
            send a[1] to console
            if 2 > 1 {
                send 1.0 to player Maple
            }
            """.trimIndent()
        val tokens = Lexer.lex(source)
        println(tokens.content)
    }

}
