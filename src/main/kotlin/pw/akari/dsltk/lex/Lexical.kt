package pw.akari.dsltk.lex

enum class TokenType {
    WORD, // Unquoted string
    VAR_ID, // 直接在 tokenize这里把类似 $id 这种的 parse 成 var id
    STRING, // quoted string
    NUMBER, // [0-9]+('.' [0-9]+)?

    SEMICOLON, COLON, COMMA, DOT, // ; : , .
    BRACE_L, BRACE_R, BRACKET_L, BRACKET_R, PAREN_L, PAREN_R, // 大中小 左右 括号

    ADD, SUB, MUL, DIV, MOD,
    AND, OR, NOT,
    GT, GEQ, LT, LEQ, EQ, NEQ,
    ASSIGN,

    KEY_TRUE, KEY_FALSE,
    KEY_IF, KEY_WHILE, KEY_BREAK, KEY_CONTINUE,
    KEY_PANIC, KEY_PRINT,

    EOF,
    ;
}

data class Token(val type: TokenType, val text: String) {
    override fun toString() = "[$type: $text]"

    companion object {
        // common tokens
        @JvmStatic val TRUE = Token(TokenType.KEY_TRUE, "true")
        @JvmStatic val FALSE = Token(TokenType.KEY_FALSE, "false")

        @JvmStatic val IF = Token(TokenType.KEY_IF, "if")
        @JvmStatic val WHILE = Token(TokenType.KEY_WHILE, "while")
        @JvmStatic val CONTINUE = Token(TokenType.KEY_CONTINUE, "continue")
        @JvmStatic val BREAK = Token(TokenType.KEY_BREAK, "break")
        @JvmStatic val PANIC = Token(TokenType.KEY_PANIC, "panic")
        @JvmStatic val PRINT = Token(TokenType.KEY_PRINT, "print")

        @JvmStatic val PAREN_L = Token(TokenType.PAREN_L, "(")
        @JvmStatic val PAREN_R = Token(TokenType.PAREN_R, ")")
        @JvmStatic val BRACKET_L = Token(TokenType.BRACKET_L, "[")
        @JvmStatic val BRACKET_R = Token(TokenType.BRACKET_R, "]")
        @JvmStatic val BRACE_L = Token(TokenType.BRACE_L, "{")
        @JvmStatic val BRACE_R = Token(TokenType.BRACE_R, "}")

        @JvmStatic val ADD = Token(TokenType.ADD, "+")
        @JvmStatic val SUB = Token(TokenType.SUB, "-")
        @JvmStatic val MUL = Token(TokenType.MUL, "*")
        @JvmStatic val DIV = Token(TokenType.DIV, "/")
        @JvmStatic val MOD = Token(TokenType.MOD, "%")

        @JvmStatic val AND = Token(TokenType.AND, "&")
        @JvmStatic val OR = Token(TokenType.OR, "|")
        @JvmStatic val NOT = Token(TokenType.NOT, "!")

        @JvmStatic val GT = Token(TokenType.GT, ">")
        @JvmStatic val GEQ = Token(TokenType.GEQ, ">=")
        @JvmStatic val LT = Token(TokenType.LT, "<")
        @JvmStatic val LEQ = Token(TokenType.LEQ, "<=")
        @JvmStatic val EQ = Token(TokenType.EQ, "==")
        @JvmStatic val NEQ = Token(TokenType.NEQ, "!=")

        @JvmStatic val ASSIGN = Token(TokenType.ASSIGN, "=")

        @JvmStatic val SEMICOLON = Token(TokenType.SEMICOLON, ";")
        @JvmStatic val COLON = Token(TokenType.COLON, ":")
        @JvmStatic val COMMA = Token(TokenType.COMMA, ",")
        @JvmStatic val DOT = Token(TokenType.DOT, ".")

        @JvmStatic val EOF = Token(TokenType.EOF, "<EOF>")
    }
}

data class TokenStream(val content: List<Token>, var offset: Int = 0) {
    val size = content.size

    fun hasNext() = offset < size

    fun next() = content[offset++]
    fun peek() = content[offset]

    fun skip() { offset++ }
    fun skip(n: Int) { offset += n }
    fun reset() { offset = 0 }
    fun reset(offset: Int) { this.offset = offset }

    companion object {
        @JvmStatic fun TokenStream.expect(type: TokenType): Token {
            val t = next()
            if(t.type != type) throw IllegalStateException("Expected $type, got $t")
            return t
        }
        @JvmStatic fun TokenStream.expect(text: String): Token {
            val t = next()
            if(t.text != text) throw IllegalStateException("Expected $text, got $t")
            return t
        }

        @JvmStatic fun TokenStream.test(type: TokenType): Boolean {
            return peek().type == type
        }
        @JvmStatic fun TokenStream.test(text: String): Boolean {
            return peek().text == text
        }

        @JvmStatic fun TokenStream.testAndSkip(type: TokenType): Boolean {
            val t = peek()
            if(t.type == type) {
                skip()
                return true
            }
            return false
        }
        @JvmStatic fun TokenStream.testAndSkip(text: String): Boolean {
            val t = peek()
            if(t.text == text) {
                skip()
                return true
            }
            return false
        }
    }
}

object Lexer {
    fun lex(source: String): TokenStream {
        val src = source.toCharArray()
        val size = src.size
        var offset = 0
        val result = arrayListOf<Token>()

        while (offset < size) {
            while(offset < size && src[offset].isWhitespace()) offset++

            when(val c = src[offset]) {
                ';' -> result += Token.SEMICOLON .also { offset++ }
                ':' -> result += Token.COLON .also { offset++ }
                ',' -> result += Token.COMMA .also { offset++ }
                '.' -> result += Token.DOT .also { offset++ }

                '(' -> result += Token.PAREN_L .also { offset++ }
                ')' -> result += Token.PAREN_R .also { offset++ }
                '[' -> result += Token.BRACKET_L .also { offset++ }
                ']' -> result += Token.BRACKET_R .also { offset++ }
                '{' -> result += Token.BRACE_L .also { offset++ }
                '}' -> result += Token.BRACE_R .also { offset++ }

                '+' -> result += Token.ADD .also { offset++ }
                '-' -> result += Token.SUB .also { offset++ }
                '*' -> result += Token.MUL .also { offset++ }
                '/' -> result += Token.DIV .also { offset++ }
                '%' -> result += Token.MOD .also { offset++ }

                '&' -> result += Token.AND .also { offset++ }
                '|' -> result += Token.OR .also { offset++ }
                '!' -> result += Token.NOT .also { offset++ }

                '>' -> result += if (offset + 1 < size && src[offset + 1] == '=') {
                    offset += 2
                    Token.GEQ
                } else {
                    offset++
                    Token.GT
                }
                '<' -> result += if (offset + 1 < size && src[offset + 1] == '=') {
                    offset += 2
                    Token.LEQ
                } else {
                    offset++
                    Token.LT
                }
                '=' -> result += if (offset + 1 < size && src[offset + 1] == '=') {
                    offset += 2
                    Token.EQ
                } else {
                    offset++
                    Token.ASSIGN
                }

                else -> when {
                    c.isNumber() -> {
                        val start = offset
                        while (offset < size && src[offset].isNumber()) offset++
                        if (offset < size && src[offset] == '.') {
                            offset++
                            while (offset < size && src[offset].isNumber()) offset++
                        }
                        result += Token(TokenType.NUMBER, String(src.copyOfRange(start, offset)))
                    }
                    c.isWordStart() -> {
                        val start = offset
                        while (offset < size && src[offset].isWordBody()) offset++
                        result += toKeyword(Token(TokenType.WORD, String(src.copyOfRange(start, offset))))
                    }
                    c == '$' -> {
                        val start = offset + 1
                        while (offset < size && src[offset].isWordBody()) offset++
                        result += Token(TokenType.VAR_ID, String(src.copyOfRange(start, offset)))
                    }
                    c == '"' -> {
                        offset++
                        val start = offset
                        while (offset < size && src[offset] != '"') offset++
                        result += Token(TokenType.STRING, String(src.copyOfRange(start, offset)))
                        offset++
                    }
                    c == '\'' -> {
                        offset++
                        val start = offset
                        while (offset < size && src[offset] != '\'') offset++
                        result += Token(TokenType.STRING, String(src.copyOfRange(start, offset)))
                        offset++
                    }

                    else -> {
                        println("Unknown char: $c")
                        offset++
                    }
                }
            }
        }

        result += Token.EOF
        return TokenStream(result)
    }

    private inline fun toKeyword(token: Token): Token = when(token.text) {
        "if" -> Token.IF
        "while" -> Token.WHILE
        "continue" -> Token.CONTINUE
        "break" -> Token.BREAK
        "panic" -> Token.PANIC
        "print" -> Token.PRINT
        "true" -> Token.TRUE
        "false" -> Token.FALSE
        else -> token
    }

    private inline fun Char.isWhitespace() = this == ' ' || this == '\t' || this == '\n' || this == '\r'
    private inline fun Char.isNumber() = this in '0'..'9'
    private inline fun Char.isWordStart() = this in 'A'..'Z' || this in 'a'..'z' || this == '_'
    private inline fun Char.isWordBody() = this.isWordStart() || this.isNumber()
}



