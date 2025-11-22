package parser
import scanner.*

class Parser(private val tokens: List<Token>) {
    private var current = 0
    private var hadError = false

    fun parseProgram(): Stmt.Program {
        hadError = false
        val statements = parseStatements()
        return Stmt.Program(statements)
    }

    private fun parseStatements(): Array<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            val stmt = statement()
            if (!hadError) {
                statements.add(stmt)
            } else {
                synchronize()
            }
        }
        return statements.toTypedArray()
    }
    
    private fun statement(): Stmt = when {
        match(TokenType.PRINT) -> printStatement()
        match(TokenType.VAR) -> varDeclaration()
        match(TokenType.SUGOD) -> blockStatement() 
        else -> expressionStatement()
    }

    private fun blockStatement(): Stmt.Block {
        val stmts = parseBlockStatements()
        if (!hadError) {
            if (consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang block bai.") == null) {
                return Stmt.Block(emptyArray())
            }
            if (consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa block bai") == null) {
                return Stmt.Block(emptyArray())
            }
        }
        return Stmt.Block(stmts)
    }

    private fun parseBlockStatements(): Array<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            val stmt = statement()
            if (!hadError) {
                statements.add(stmt)
            } else {
                synchronize()
            }
        }
        return statements.toTypedArray()
    }

    private fun printStatement(): Stmt {
        val expr = expression()
        if (!hadError) {
            if (consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan bai") == null) {
                return Stmt.Print(Expr.Literal(null))
            }
        }
        return Stmt.Print(expr)
    }

    private fun varDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Dapat naay variable name")
        if (hadError || name == null) {
            return Stmt.Var(Token(TokenType.IDENTIFIER, "ERROR", null, 0), null)
        }
        
        val initializer = if (match(TokenType.EQUALS)) expression() else null
        
        if (!hadError) {
            if (consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa var declaration bai") == null) {
                return Stmt.Var(name, initializer)
            }
        }
        return Stmt.Var(name, initializer)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        if (!hadError) {
            if (consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa expression bai") == null) {
                return Stmt.ExprStmt(expr)
            }
        }
        return Stmt.ExprStmt(expr)
    }

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = equality()
        
        if (match(TokenType.EQUALS)) {
            val equals = previous()
            val value = assignment()
            
            if (expr is Expr.Variable) {
                return Expr.Assign(expr.name, value)  
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

    private fun primary(): Expr {
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.TRUE, TokenType.FALSE, TokenType.NULL)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            if (!check(TokenType.RIGHT_PAREN)) {
                reportError(peek(), "Wa nay ')' sa katapusan bai. Naay sobra nga '('.")
                return Expr.Literal("ERROR") 
            }
            if (consume(TokenType.RIGHT_PAREN, "Dapat naay ')' sa katapusan bai.") == null) {
                return Expr.Literal("ERROR")
            }
            return Expr.Grouping(expr)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        reportError(peek(), "Tarungi ang expression bai.")
        return Expr.Literal("ERROR") 
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

    private fun consume(type: TokenType, message: String): Token? {
        if (check(type)) {
            return advance()
        }
        reportError(peek(), message)
        return null
    }

    private fun reportError(token: Token, message: String) {
        println("[Line ${token.line}] Error sa '${token.lexeme}': $message")
        hadError = true
    }

    private fun synchronize() {
        advance() 
        
        while (!isAtEnd()) {
            if (previous().type == TokenType.PERIOD) return // end of statement
            
            when (peek().type) {
                TokenType.PRINT, TokenType.VAR, TokenType.SUGOD -> return
                else -> advance()
            }
        }
    }
}