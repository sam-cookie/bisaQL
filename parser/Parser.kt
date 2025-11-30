package parser

import scanner.*
import errorhandling.RuntimeError

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parseProgram(): Stmt.Program {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(statement())
        }
        return Stmt.Program(statements)
    }

    private fun statement(): Stmt {
        return when {
            // Note: Make sure checkSequence uses TokenType.VAR if you removed UG from here
            checkSequence(TokenType.PAGHIMO, TokenType.VAR) -> varDeclaration()
            check(TokenType.USBA) -> assignmentStatement()
            check(TokenType.PRINT) -> printStatement()
            check(TokenType.SUGOD) -> blockStatement()
            else -> expressionStatement()
        }
    }

    private fun varDeclaration(): Stmt {
        consume(TokenType.PAGHIMO, "Dapat magsugod sa 'paghimo'")
        // consume(TokenType.UG, "Dapat naay 'ug'") 
        consume(TokenType.VAR, "Dapat naay 'bar'")
        val name = consume(TokenType.IDENTIFIER, "Dapat naay variable name")
        consume(TokenType.NGA, "Dapat naay 'nga' before ang value")
        val initializer = expression()
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan")
        return Stmt.Var(name, initializer)
    }

    private fun assignmentStatement(): Stmt {
        consume(TokenType.USBA, "Dapat magsugod sa 'usba'")
        consume(TokenType.ANG, "Dapat naay 'ang'")
        val name = consume(TokenType.IDENTIFIER, "Dapat naay variable name")

        val value: Expr

        if (match(TokenType.HIMUAG)) {
            value = expression()
        } 
        else if (match(TokenType.ADD_ASSIGN)) { // +=
            val operator = Token(TokenType.PLUS, "+", null, previous().line)
            val increment = expression()
            value = Expr.Binary(Expr.Variable(name), operator, increment)
        } 
        else if (match(TokenType.MINUS_ASSIGN)) { // -=
            val operator = Token(TokenType.MINUS, "-", null, previous().line)
            val decrement = expression()
            value = Expr.Binary(Expr.Variable(name), operator, decrement)
        } 
        else if (match(TokenType.TIMES_ASSIGN)) { // *=
            val operator = Token(TokenType.TIMES, "*", null, previous().line)
            val factor = expression()
            value = Expr.Binary(Expr.Variable(name), operator, factor)
        } 
        else if (match(TokenType.DIVIDED_ASSIGN)) { // /=
            val operator = Token(TokenType.DIVIDE, "/", null, previous().line)
            val divisor = expression()
            value = Expr.Binary(Expr.Variable(name), operator, divisor)
        } 
        else {
            throw RuntimeError("Gipaabot ang 'himuag', 'dugangig', 'kuhaaig', 'piluag', o 'bahinag'.", peek().line)
        }

        consume(TokenType.PERIOD, "Dapat period sa katapusan")
        return Stmt.Assign(name, value)
    }

    private fun printStatement(): Stmt {
        consume(TokenType.PRINT, "Dapat magsugod sa 'ipakita'")
        consume(TokenType.ANG, "Dapat naay 'ang'")
        val expr = expression()
        consume(TokenType.PERIOD, "Dapat naay sa katapusan")
        return Stmt.Print(expr)
    }

    private fun blockStatement(): Stmt.Block {
        consume(TokenType.SUGOD, "Dapat magsugod sa 'sugod'")
        val stmts = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            stmts.add(statement())
        }
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang block")
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan")
        return Stmt.Block(stmts)
    }

    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan")
        return Stmt.ExprStmt(expr)
    }

    private fun expression(): Expr = assignmentExpr()

    private fun assignmentExpr(): Expr {
        val expr = logicOr()

        if (match(TokenType.EQUALS)) {
            val equals = previous()
            val value = assignmentExpr()
            if (expr is Expr.Variable) return Expr.Assign(expr.name, value)
            throw RuntimeError("Mali nga assignment target bai.", equals.line)
        }
        return expr
    }

    private fun logicOr(): Expr {
        var expr = logicAnd()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right = logicAnd()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun logicAnd(): Expr {
        var expr = equality()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (true) {
            
            // "parehas" (==)
            if (match(TokenType.EQUALTO)) {
                val operator = previous()
                val right = comparison()
                expr = Expr.Binary(expr, operator, right)
                continue
            }

            // "dili parehas" (!=)
            if (check(TokenType.NOT) && peekNext().type == TokenType.EQUALTO) {
                val startToken = advance() 
                consume(TokenType.EQUALTO, "Ga-expect ug 'parehas' after 'dili'.") 
                
                val operator = Token(TokenType.NOT_EQUAL, "dili parehas", null, startToken.line)
                
                val right = comparison()
                expr = Expr.Binary(expr, operator, right)
                continue
            }

            break
        }
        return expr
    }
    
    private fun comparison(): Expr {
        var expr = stringConcat()
        while (check(TokenType.MAS)) {
            val operator = parseMasOperator()
            val right = stringConcat()
            expr = Expr.Binary(expr, operator, right)
        }
        return expr
    }

    private fun parseMasOperator(): Token {
        val startToken = consume(TokenType.MAS, "Expect 'mas' keyword.")
        if (match(TokenType.DAKOS)) {
            if (match(TokenType.TUPONG)) {
                return Token(TokenType.GREATER_THAN_EQUAL, "mas dakos tupongs", null, startToken.line)
            }
            return Token(TokenType.GREATER_THAN, "mas dakos", null, startToken.line)
        }
        if (match(TokenType.GAMAYS)) {
            if (match(TokenType.TUPONG)) {
                return Token(TokenType.LESS_THAN_EQUAL, "mas gamays tupongs", null, startToken.line)
            }
            return Token(TokenType.LESS_THAN, "mas gamays", null, startToken.line)
        }

        throw RuntimeError("Naay gipaabot nga 'dakos' o 'gamays' human sa 'mas'.", startToken.line)
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
        if (match(TokenType.STRING)) {
            return Expr.Literal(previous().literal!!) 
        }

        if (match(TokenType.NUMBER, TokenType.TRUE, TokenType.FALSE, TokenType.NULL)) {
            return Expr.Literal(previous().literal!!)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous()) 
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Dili balanced ang parentheses.")
            return Expr.Grouping(expr)
        }

        throw RuntimeError("Tarungi ang expression. Found unexpected token: ${peek().type}", peek().line)
    }

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

    private fun checkSequence(vararg types: TokenType): Boolean {
        if (current + types.size - 1 >= tokens.size) return false
        for ((i, t) in types.withIndex()) {
            if (tokens[current + i].type != t) return false
        }
        return true
    }

    private fun peekNext() = if (current + 1 >= tokens.size) tokens.last() else tokens[current + 1]

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
}