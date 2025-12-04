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
            check(TokenType.FUNCTION) -> funDeclaration()
            checkSequence(TokenType.PAGHIMO, TokenType.VAR) -> varDeclaration()
            check(TokenType.USBI) -> assignmentStatement()
            check(TokenType.PRINT) -> printStatement()
            check(TokenType.SUGOD) -> blockStatement()
            check(TokenType.WHILE) -> whileStatement()
            check(TokenType.IF) -> ifStatement()
            check(TokenType.ELSE) -> {
                consume(TokenType.IF, "Dapat naay 'kung' bago ang ugdi") 
                elseStatement()}
            check(TokenType.RETURN) -> returnStatement()
            else -> expressionStatement()
        }
    }

    private fun varDeclaration(): Stmt {
        consume(TokenType.PAGHIMO, "Dapat magsugod sa 'Paghimog'")
        consume(TokenType.VAR, "Dapat naay 'bar'")
        val name = consume(TokenType.IDENTIFIER, "Dapat naay variable name")
        consume(TokenType.NGA, "Dapat naay 'nga' before ang value")
        val initializer = expression()
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan")
        return Stmt.Var(name, initializer)
    }

    private fun assignmentStatement(): Stmt {
        consume(TokenType.USBI, "Dapat magsugod sa 'usbi'")
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
            throw RuntimeError("Mali nga assignment keyword ang gamit nimo bai: dugangig, kuhaag, piluag, bahinig", peek().line)
        }

        consume(TokenType.PERIOD, "Dapat period sa katapusan")
        return Stmt.Assign(name, value)
    }

    private fun printStatement(): Stmt {
        consume(TokenType.PRINT, "Dapat magsugod sa 'Ipakita'")
        consume(TokenType.ANG, "Dapat naay 'ang'")
        val expr = expression()
        consume(TokenType.PERIOD, "Dapat naay (.) sa katapusan")
        return Stmt.Print(expr)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.WHILE, "Dapat magsugod sa 'samtang'")
        consume(TokenType.ANG, "Dapat naay 'ang' before ang condition")
        val condition = expression()
        consume(TokenType.BUHATA, "Dapat naay 'buhata' after condition")
        consume(TokenType.COMMA, "Dapat naay comma human sa 'buhata'")
        val stmts = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            stmts.add(statement())
        }
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para sa while block")
        consume(TokenType.PERIOD, "Dapat 'tapos.' ang ending")
        return Stmt.While(condition, stmts)
    }

    private fun funDeclaration(): Stmt.Fun {
        consume(TokenType.FUNCTION, "Dapat magsugod sa 'Buhatag'")
        val name = consume(TokenType.IDENTIFIER, "Dapat naay function name")
        consume(TokenType.ANG, "Expected 'ang' after function name.")
        // require opening parenthesis
        consume(TokenType.LEFT_PAREN, "Dapat naay '(' after function name")

        val params = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) { // parameters exist
            do {
                params.add(consume(TokenType.IDENTIFIER, "Dapat naay parameter name"))
            } while (match(TokenType.COMMA))
        }

        consume(TokenType.RIGHT_PAREN, "Dapat naay ')' after parameters")
        consume(TokenType.COMMA, "Dapat naay ',' before function body")

        val body = functionBlockStatement()
        return Stmt.Fun(name, params, body)
    }

    private fun functionBlockStatement(): Stmt.Block {
        val stmts = mutableListOf<Stmt>()

        // read until Tapos or Tapos.
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            stmts.add(statement())
        }

        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang function block")
        consume(TokenType.PERIOD, "Dapat 'tapos.' ang katapusan")

        return Stmt.Block(stmts)
    }

    private fun callStatement(): Stmt.Call {
        consume(TokenType.CALL, "Dapat magsugod sa 'Tawagi'")
        consume(TokenType.ANG, "Expected 'ang' after 'Tawagi'")
        val functionName = consume(TokenType.IDENTIFIER, "Dapat naay function name")
        consume(TokenType.KAY, "Expected 'kay' before arguments")

        val args = mutableListOf<Expr>()
        do {
            args.add(expression())
        } while (match(TokenType.COMMA)) // support multiple arguments

        consume(TokenType.PERIOD, "Dapat naay period sa katapusan")
        return Stmt.Call(functionName, args)
    }


    private fun ifStatement(): Stmt {
        consume(TokenType.IF, "Dapat magsugod sa 'kung'")
        consume(TokenType.ANG, "Dapat naay 'ang' bago ang condition")
        val condition = expression()
        consume(TokenType.BUHATA, "Dapat naay 'buhata' pagtapos ng condition")
        consume(TokenType.COMMA, "Dapat naay comma human sa 'buhata'")
        val body = mutableListOf<Stmt>()
        while (!check(TokenType.ELSE) && !check(TokenType.TAPOS) && !isAtEnd()) {
            body.add(statement())
        }
        var elseBranch: Stmt? = null
        if (match(TokenType.ELSE)) {
            elseBranch = elseStatement()
        }
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang if-block")
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan ng kung")
        return Stmt.If(condition, body, elseBranch)
    }

    private fun elseStatement(): Stmt {
        //consume(TokenType.IF, "Dapat naay 'kung' bago ang ugdi")
        val stmts = mutableListOf<Stmt>()
        while (!check(TokenType.TAPOS) && !isAtEnd()) {
            stmts.add(statement())
        }
        consume(TokenType.TAPOS, "Dapat naay 'tapos' para matapos ang block")
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan ng ugdi")
        return Stmt.Block(stmts)
    }

    private fun returnStatement(): Stmt {
        val keyword = consume(TokenType.RETURN, "Dapat magsugod sa 'Ibalik'")
        consume(TokenType.ANG, "DApat naay 'ang' paghuman sa ibalik!")
        val value: Expr? = if (!check(TokenType.PERIOD)) expression() else null
        consume(TokenType.PERIOD, "Dapat naay period sa katapusan sa return statement")
        return Stmt.Return(keyword, value)
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
        if (check(TokenType.CALL)) { // CALL = "Tawagi"
            return callStatement()
        }
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
                // parse only a single value for Boolean comparison
                val right = stringConcat()
                expr = Expr.Binary(expr, operator, right)
                continue
            }

            // "dili parehas" (!=)
            if (check(TokenType.NOT) && peekNext().type == TokenType.EQUALTO) {
                val startToken = advance() 
                consume(TokenType.EQUALTO, "Ga-expect ug 'parehas' after 'dili'.") 
                
                val operator = Token(TokenType.NOT_EQUAL, "dili parehas", null, startToken.line)

                val right = stringConcat()
                expr = Expr.Binary(expr, operator, right)
                continue
            }

            break
        }
        return expr
    }

    
    private fun comparison(): Expr {
        var expr = stringConcat()
        while (check(TokenType.MAS)) { // Ipakita ang x 
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
        return when {
            match(TokenType.IDENTIFIER) -> {
                // variable or function call
                parseCallOrVariable(Expr.Variable(previous()))
            }

            match(TokenType.LEFT_PAREN) -> {
                val expr = expression()

                if (isAtEnd() || !check(TokenType.RIGHT_PAREN)) {
                    throw RuntimeError("Sa primary, dili balanced ang parentheses", peek().line)
                }

                advance() // consume the RIGHT_PAREN
                Expr.Grouping(expr)
            }

            match(TokenType.STRING) -> Expr.Literal(previous().literal!!)
            match(TokenType.NUMBER, TokenType.TRUE, TokenType.FALSE, TokenType.NULL) -> Expr.Literal(previous().literal!!)
            else -> throw RuntimeError("Tarungi ang expression. Found unexpected token: ${peek().type}", peek().line)
        }
    }

    private fun parseCallOrVariable(callee: Expr): Expr {
        var expr = callee

        while (true) {
            if (check(TokenType.LEFT_PAREN)) {
                val openParen = advance() // consume '('
                val args = parseArguments(TokenType.RIGHT_PAREN)
                expr = Expr.Call(expr, openParen, args)
            } 
            else if (check(TokenType.KAY)) {
                val kayToken = advance() // consume 'KAY'
                val args = mutableListOf<Expr>()
                while (!isAtEnd() && !check(TokenType.PERIOD)) {
                    args.add(expression())
                    match(TokenType.COMMA) // optional comma
                }
                expr = Expr.Call(expr, kayToken, args)
            } 
            else break
        }

        return expr
    }

    private fun parseArguments(closing: TokenType): List<Expr> {
        val args = mutableListOf<Expr>()
        if (!check(closing)) {
            do {
                args.add(expression())
            } while (match(TokenType.COMMA))
        }
        consume(closing, "Dili balanced ang parentheses sa function call")
        return args
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