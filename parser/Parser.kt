package parser
import scanner.*

class Parser(private val tokens: List<Token>) {
    private var current = 0

    // Add this public method
    fun parseProgram(): Stmt.Program = Stmt.Program(parseStatements())

    private fun parseStatements(): Array<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(statement())
        }
        return statements.toTypedArray()
    }
    
    private fun statement(): Stmt = when {
        match(TokenType.PRINT) -> printStatement()
        match(TokenType.VAR) -> varDeclaration()
        match(TokenType.SUGOD) -> blockStatement() 
        else -> expressionStatement()
    }

    // Single blockStatement function for SUGOD/TAPOS
    private fun blockStatement(): Stmt.Block {
        // SUGOD is already consumed, now parse the block statements
        val stmts = parseBlockStatements()
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang block bai.")
        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa block bai")
        return Stmt.Block(stmts)
    }

    // Single parseBlockStatements function for TAPOS
    private fun parseBlockStatements(): Array<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            statements.add(statement())
        }
        return statements.toTypedArray()
    }

    // single statements ends w periods 
    private fun printStatement(): Stmt {
        val expr = expression()
        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan bai")
        return Stmt.Print(expr)
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Dapat naay variable name")
        val initializer = if (match(TokenType.EQUALS)) expression() else null
        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa var declaration bai")
        return Stmt.Var(name, initializer)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa expression bai")
        return Stmt.ExprStmt(expr)
    }

    // expressions
    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = equality()
        
        if (match(TokenType.EQUALS)) {
            val equals = previous()
            val value = assignment()
            
            if (expr is Expr.Variable) {
                val name = expr.name
                return Expr.Binary(expr, equals, value) 
            }
            
            reportError(equals, "Invalid assignment target bai.")
        }
        
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.EQUALTO, TokenType.NOT_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr = stringConcat()
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL,
                     TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL)) {
            val operator = previous()
            val right = stringConcat()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun stringConcat(): Expr {
        var expr = term()
        while (match(TokenType.SUMPAY)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.TIMES, TokenType.DIVIDE, TokenType.MODULO)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr = when {
        match(TokenType.NUMBER, TokenType.STRING, TokenType.TRUE, TokenType.FALSE, TokenType.NULL) ->
            Expr.Literal(previous().literal)

        match(TokenType.LEFT_PAREN) -> {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Dapat naay ')' sa katapusan bai.")
            Expr.Grouping(expr)
        }

        match(TokenType.IDENTIFIER) ->
            Expr.Variable(previous())

        else -> {
            reportError(peek(), "Tarungi ang expression bai.")
            Expr.Literal("nil")
        }
    }

    // helpers 
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean = !isAtEnd() && peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == TokenType.EOF
    private fun peek(): Token = tokens[current]
    private fun previous(): Token = tokens[current - 1]
    private fun peekNext(): Token? = if (current + 1 < tokens.size) tokens[current + 1] else null

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw RuntimeException("[Line ${peek().line}] Error: $message")
    }

    private fun reportError(token: Token, message: String) {
        println("[Line ${token.line}] Error sa '${token.lexeme}': $message")
    }
}