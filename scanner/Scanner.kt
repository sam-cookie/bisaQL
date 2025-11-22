package scanner

class Scanner(
    private val source: String,
    private var index: Int = 0,
    private var line: Int = 1
) {

    private val tokens = mutableListOf<Token>()

    fun scanToken(source: String, index: Int): Pair<TokenType?, Int> {
        val c = source[index]
        val next = if (index + 1 < source.length) source[index + 1] else '\u0000'

        return when (c) {
            '.' -> TokenType.PERIOD to 1
            '(' -> TokenType.LEFT_PAREN to 1
            ')' -> TokenType.RIGHT_PAREN to 1
            '{' -> TokenType.LEFT_BRACE to 1
            '}' -> TokenType.RIGHT_BRACE to 1
            '+' -> if (next == '=') TokenType.ADD_ASSIGN to 2 else TokenType.PLUS to 1
            '-' -> if (next == '=') TokenType.MINUS_ASSIGN to 2 else TokenType.MINUS to 1
            '*' -> if (next == '=') TokenType.TIMES_ASSIGN to 2 else TokenType.TIMES to 1
            '/' -> if (next == '=') TokenType.DIVIDED_ASSIGN to 2 else TokenType.DIVIDE to 1
            '%' -> if (next == '=') TokenType.MODULO_ASSIGN to 2 else TokenType.MODULO to 1
            '=' -> if (next == '=') TokenType.EQUALTO to 2 else TokenType.EQUALS to 1
            '!' -> if (next == '=') TokenType.NOT_EQUAL to 2 else TokenType.NOT to 1
            '&' -> if (next == '&') TokenType.LOGICAL_AND to 2 else TokenType.AND to 1
            '|' -> if (next == '|') TokenType.LOGICAL_OR to 2 else TokenType.OR to 1
            ',' -> TokenType.COMMA to 1
            ':' -> TokenType.COLON to 1
            '<' -> if (next == '=') TokenType.LESS_THAN_EQUAL to 2 else TokenType.LESS_THAN to 1
            '>' -> if (next == '=') TokenType.GREATER_THAN_EQUAL to 2 else TokenType.GREATER_THAN to 1
            '"' -> TokenType.DOUBLE_QUOTE to 1
            else -> null to 1
        }
    }

    fun scanLiterals(source: String, index: Int, line: Int): Pair<TokenType?, Int> {
        val c = source[index]

        // number literal
        if (c.isDigit()) {
            var length = 1
            while (index + length < source.length && source[index + length].isDigit()) length++
            if (index + length < source.length && source[index + length] == '.') {
                if (index + length + 1 < source.length && source[index + length + 1].isDigit()) {
                    length++
                    while (index + length < source.length && source[index + length].isDigit()) length++
                }
            }

            val numStart = source.substring(index, index + length)
            // FIXED: Only check for identifier characters immediately after number
            if (index + length < source.length && (source[index + length].isLetter() || source[index + length] == '_')) {
                println("[Line $line] Mali ang starting number nga '$numStart' sa identifier")
            }

            return TokenType.NUMBER to length
        }

        // string literal
        if (c == '"') {
            var length = 1
            while (index + length < source.length && source[index + length] != '"') length++
            if (index + length < source.length) {
                length++
                return TokenType.STRING to length
            } else {
                println("[Line $line] Error sa katapusan: Dapat naay '\"' sa katapusan bai.")
                return null to length
            }
        }

        // identifier or keyword
        if (c.isLetter() || c == '_') {
            var length = 1
            while (index + length < source.length && (source[index + length].isLetterOrDigit() || source[index + length] == '_')) {
                length++
            }

            val lexeme = source.substring(index, index + length)
            val type = keywords[lexeme] ?: TokenType.IDENTIFIER
            return type to length
        }

        return null to 1
    }

    fun scanOtherCharacters(source: String, startIndex: Int, startLine: Int) {
        var index = startIndex
        var line = startLine
        tokens.clear()

        while (index < source.length) {
            val c = source[index]

            // Handle Windows line endings: \r\n
            if (c == '\r' && index + 1 < source.length && source[index + 1] == '\n') {
                // Skip the \r and process the \n on next iteration
                index++
                continue
            }

            // Skip whitespace (including newlines since we're using periods)
            if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
                index++
                continue
            }

            // PERIOD as statement terminator
            if (c == '.') {
                tokens.add(Token(TokenType.PERIOD, ".", null, line))
                index++
                continue
            }

            // Block comments
            if (index + 2 < source.length && source.substring(index, index + 2) == "/*") {
                val closing = source.indexOf("*/", index + 2)
                val endComment = if (closing != -1) closing + 2 else source.length
                val lexeme = source.substring(index, endComment)
                line += lexeme.count { it == '\n' }
                index = endComment
                if (closing == -1) println("[Line $line] Error sa katapusan: Dapat naay '*/' para matapos ang comment bai.")
                continue
            }

            // Line comments
            if (index + 1 < source.length && source.substring(index, index + 2) == "//") {
                val closing = source.indexOf("\n", index + 2)
                index = if (closing != -1) closing else source.length
                continue
            }

            // Literals (numbers, strings, identifiers, keywords)
            if (isLiteral(source, index, line, tokens)) {
                val (_, litLen) = scanLiterals(source, index, line)
                index += litLen
                continue
            }

            // Symbols (operators, punctuation)
            if (isSymbol(source, index, line, tokens)) {
                val (_, symLen) = scanToken(source, index)
                index += symLen
                continue
            }

            // Unknown character
            println("[Line $line] Mali ang '$c' nga character bai.")
            index++
        }

        // End of file
        tokens.add(Token(TokenType.EOF, "", null, line))
    }

    fun isLiteral(source: String, index: Int, line: Int, tokens: MutableList<Token>): Boolean {
        val (litType, litLen) = scanLiterals(source, index, line)
        if (litType != null) {
            val lexeme = source.substring(index, index + litLen)
            val literal = when (litType) {
                TokenType.NUMBER -> if (lexeme.contains(".")) lexeme.toDouble() else lexeme.toInt()
                TokenType.STRING -> lexeme.substring(1, lexeme.length - 1)
                TokenType.TRUE -> true
                TokenType.FALSE -> false
                TokenType.NULL -> null
                else -> null
            }
            tokens.add(Token(litType, lexeme, literal, line))
            return true
        }
        return false
    }

    fun isSymbol(source: String, index: Int, line: Int, tokens: MutableList<Token>): Boolean {
        val (symType, symLen) = scanToken(source, index)
        if (symType != null) {
            val lexeme = source.substring(index, index + symLen)
            tokens.add(Token(symType, lexeme, null, line))
            return true
        }
        return false
    }

    fun scanTokens(): List<Token> {
        tokens.clear()
        scanOtherCharacters(source, 0, 1)
        return tokens
    }
}