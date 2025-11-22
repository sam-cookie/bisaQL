package Evaluator
import parser.*
import scanner.*

class Environment(val enclosing: Environment? = null) {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun assign(name: String, value: Any?): Any? {
        if (values.containsKey(name)) {
            values[name] = value
            return value
        } else if (enclosing != null) {
            return enclosing.assign(name, value)
        } else {
            println("[Runtime error] Wa manay variable '$name' bai.")
            return null
        }
    }

    fun get(name: String): Any? {
        return if (values.containsKey(name)) {
            values[name]
        } else if (enclosing != null) {
            enclosing.get(name)
        } else {
            println("[Runtime error] Wa ma-define nga variable '$name' bai.")
            return null
        }
    }
}

class Evaluator(private val isReplMode: Boolean = false) {
    private var environment = Environment()

    // execute the whole program
    fun executeProgram(program: Stmt.Program) {
        for (stmt in program.statements) {
            execute(stmt)
        }
    }

    // STATEMENTS
    fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Var -> {
                val value = stmt.initializer?.let { evaluate(it) } ?: "waay"
                environment.define(stmt.name.lexeme, value)
            }

            is Stmt.Assign -> {
                val value = evaluate(stmt.value)
                environment.assign(stmt.name.lexeme, value)
            }

            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(valueToString(value))
            }

            is Stmt.ExprStmt -> {
                val result = evaluate(stmt.expression)
                // REPL MODE: Auto-print expression results
                if (isReplMode) {
                    println(valueToString(result))
                }
                // In script mode, the result is silently discarded
            }

            is Stmt.Block -> {
                executeBlock(stmt.statements, Environment(environment))
            }

            is Stmt.Program -> executeProgram(stmt)
        }
    }

    fun executeBlock(statements: Array<Stmt>, blockEnv: Environment) {
        val previous = environment
        environment = blockEnv
        try {
            for (s in statements) execute(s)
        } finally {
            environment = previous
        }
    }

    // EXPRESSIONS
    fun evaluate(expr: Expr?): Any? {
        if (expr == null) return null
        
        val result = when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> evaluateUnary(expr)
            is Expr.Binary -> evaluateBinary(expr)
            is Expr.Variable -> environment.get(expr.name.lexeme)
        }
        
        return when (result) {
            true -> "tuod"
            false -> "atik" 
            null -> "waay"
            else -> result
        }
    }

    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.MINUS -> {
                if (right is Number) -right.toDouble()
                else {
                    runtimeError(expr.operator, "Number dapat ang operand sa unary '-' bai.")
                    null
                }
            }
            TokenType.NOT -> !isTruthy(right)
            else -> null
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.PLUS -> {
                if (left is Number && right is Number) left.toDouble() + right.toDouble()
                else if (left is String && right is String) left + right
                else {
                    runtimeError(expr.operator, "Ang '+' kay para lang sa numbers ug strings bai.")
                    null
                }
            }
            TokenType.MINUS -> {
                if (left is Number && right is Number) left.toDouble() - right.toDouble()
                else {
                    runtimeError(expr.operator, "Di ni pwede bai! Number dapat ang operand")
                    null
                }
            }
            TokenType.TIMES -> {
                if (left is Number && right is Number) left.toDouble() * right.toDouble()
                else {
                    runtimeError(expr.operator, "Di ni pwede bai! Number dapat ang operand")
                    null
                }
            }
            TokenType.DIVIDE -> {
                if (left is Number && right is Number) {
                    if (right.toDouble() == 0.0) {
                        runtimeError(expr.operator, "Bawal magdivide by 0 bai. Tadlong bala!")
                        null
                    } else left.toDouble() / right.toDouble()
                } else {
                    runtimeError(expr.operator, "Di ni pwede bai! Number dapat ang operand")
                    null
                }
            }
            TokenType.SUMPAY -> {
                if (left is String && right is String) left + right
                else {
                    runtimeError(expr.operator, "Ang 'sumpay' kay para lang sa strings bai.")
                    null
                }
            }
            TokenType.GREATER_THAN ->
                compareNumbers(expr.operator, left, right) { l, r -> l > r }
            TokenType.GREATER_THAN_EQUAL ->
                compareNumbers(expr.operator, left, right) { l, r -> l >= r }
            TokenType.LESS_THAN ->
                compareNumbers(expr.operator, left, right) { l, r -> l < r }
            TokenType.LESS_THAN_EQUAL ->
                compareNumbers(expr.operator, left, right) { l, r -> l <= r }
            TokenType.EQUALTO -> isEqual(left, right)
            TokenType.NOT_EQUAL -> !isEqual(left, right)
            else -> null
        }
    }

    private fun compareNumbers(
        operator: Token,
        left: Any?,
        right: Any?,
        comparison: (Double, Double) -> Boolean
    ): Boolean? {
        return if (left is Number && right is Number) {
            comparison(left.toDouble(), right.toDouble())
        } else {
            runtimeError(operator, "Di ni pwede bai! Number dapat ang operand")
            null
        }
    }

    private fun isTruthy(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            "waay" -> false
            "tuod" -> true
            "atik" -> false
            else -> true
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    private fun valueToString(value: Any?): String {
        return when (value) {
            null -> "waay"
            true -> "tuod"
            false -> "atik"
            is Double -> if (value % 1 == 0.0) value.toInt().toString() else value.toString()
            else -> value.toString()
        }
    }

    private fun runtimeError(token: Token, message: String) {
        println("[line ${token.line}] Error bala: $message")
    }
}