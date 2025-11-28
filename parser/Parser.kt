package parser

import scanner.*
import errorhandling.HiliSayaError
import errorhandling.RuntimeError

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parseProgram(): Stmt.Program {
        val statements = parseStatements()
        return Stmt.Program(statements)
    }

    private fun parseStatements(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(statement())
        }
        return statements
    }

    private fun statement(): Stmt = when {
        match(TokenType.PRINT) -> printStatement()
        match(TokenType.VAR) -> varDeclaration()
        match(TokenType.SUGOD) -> blockStatement()
        else -> expressionStatement()
    }

    private fun blockStatement(): Stmt.Block {
        val stmts = parseBlockStatements()
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang block bai.")
        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa block bai")
        return Stmt.Block(stmts)
    }

    private fun parseBlockStatements(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            statements.add(statement())
        }
        return statements
    }

    private fun printStatement(): Stmt {
        val expr = expression()
        if (check(TokenType.RIGHT_PAREN)) {
            throw RuntimeError("Dili balanced ang parentheses bai.", peek().line)
        }
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

        if (check(TokenType.RIGHT_PAREN)) {
            throw RuntimeError("Dili balanced ang parentheses bai.", peek().line)
        }

        consume(TokenType.PERIOD, "Dapat naay period (.) sa katapusan sa expression bai")
        return Stmt.ExprStmt(expr)
    }

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = equality()
        if (match(TokenType.EQUALS)) {
            val equals = previous()
            val value = assignment()
            if (expr is Expr.Variable) return Expr.Assign(expr.name, value)
            throw RuntimeError("Invalid assignment target bai.", equals.line)
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
        while (match(
                TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL,
                TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL
            )
        ) {
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
        if (match(
                TokenType.NUMBER, TokenType.STRING,
                TokenType.TRUE, TokenType.FALSE, TokenType.NULL
            )
        ) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            if (!match(TokenType.RIGHT_PAREN)) {
                throw RuntimeError("Dili balanced ang parentheses bai.", peek().line)
            }
            return Expr.Grouping(expr)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        throw RuntimeError("Tarungi ang expression bai.", peek().line)
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

    private fun check(type: TokenType) = !isAtEnd() && peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == TokenType.EOF
    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw RuntimeError(message, peek().line)
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.PERIOD) return
            when (peek().type) {
                TokenType.PRINT, TokenType.VAR, TokenType.SUGOD -> return
                else -> advance()
            }
        }
    }
}
