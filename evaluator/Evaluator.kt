package evaluator

import parser.*
import scanner.*
import errorhandling.RuntimeError

class Evaluator {
    private var environment: Environment = Environment()

    fun executeProgram(program: Stmt.Program) {
        for (stmt in program.statements) execute(stmt)
    }

    fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Var -> {
                val value = stmt.initializer?.let { evaluate(it) } ?: null
                environment.define(stmt.name.lexeme, value)
            }

            is Stmt.Assign -> {
                val value = evaluate(stmt.value)
                environment.assign(stmt.name.lexeme, value, stmt.name.line)
            }

            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(valueToString(value))
            }

            is Stmt.ExprStmt -> {
                evaluate(stmt.expression)
            }

            is Stmt.Block -> executeBlock(stmt.statements, Environment(environment))
            
            is Stmt.Program -> executeProgram(stmt)
        }
    }

    private fun executeBlock(statements: List<Stmt>, blockEnv: Environment) {
        val previous = environment
        environment = blockEnv
        try {
            for (s in statements) execute(s)
        } finally {
            environment = previous
        }
    }

    fun evaluate(expr: Expr?): Any? {
        if (expr == null) return null

        return when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> evaluateUnary(expr)
            is Expr.Binary -> evaluateBinary(expr)
            is Expr.Variable -> environment.get(expr.name.lexeme, expr.name.line)
            is Expr.Assign -> {
                val value = evaluate(expr.value)
                environment.assign(expr.name.lexeme, value, expr.name.line)
                value
            }
        }
    }


    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.MINUS -> if (right is Number) -right.toDouble() else
                throw RuntimeError("Number dapat ang operand sa unary '-' bai.", expr.operator.line)
            TokenType.NOT -> !isTruthy(right)
            else -> null
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
           TokenType.PLUS -> when {
                left is Number && right is Number -> left.toDouble() + right.toDouble()
                left == null || right == null -> throw RuntimeError("Wa pwede ang null sa addition", expr.operator.line)
                else -> valueToString(left) + valueToString(right)
            }

            TokenType.MINUS -> checkNumbers(left, right, expr.operator) { a, b -> a - b }
            TokenType.TIMES -> checkNumbers(left, right, expr.operator) { a, b -> a * b }
            TokenType.DIVIDE -> checkNumbers(left, right, expr.operator) { a, b ->
                if (b == 0.0) throw RuntimeError("Bawal magdivide by 0 bai.", expr.operator.line)
                a / b
            }
            TokenType.MODULO -> checkNumbers(left, right, expr.operator) { a, b ->
                if (b == 0.0) throw RuntimeError("Bawal mag modulo by 0 bai.", expr.operator.line)
                a % b
            }
            TokenType.SUMPAY -> valueToString(left) + valueToString(right)
            TokenType.GREATER_THAN -> compareNumbers(left, right, expr.operator) { a, b -> a > b }
            TokenType.GREATER_THAN_EQUAL -> compareNumbers(left, right, expr.operator) { a, b -> a >= b }
            TokenType.LESS_THAN -> compareNumbers(left, right, expr.operator) { a, b -> a < b }
            TokenType.LESS_THAN_EQUAL -> compareNumbers(left, right, expr.operator) { a, b -> a <= b }
            TokenType.EQUALTO -> isEqual(left, right)
            TokenType.NOT_EQUAL -> !isEqual(left, right)
            else -> null
        }
    }

    private fun checkNumbers(left: Any?, right: Any?, operator: Token, op: (Double, Double) -> Double): Any? {
        if (left is Number && right is Number) return op(left.toDouble(), right.toDouble())
        throw RuntimeError("Number dapat ang operand bai.", operator.line)
    }

    private fun compareNumbers(left: Any?, right: Any?, operator: Token, comp: (Double, Double) -> Boolean): Boolean {
        if (left is Number && right is Number) return comp(left.toDouble(), right.toDouble())
        throw RuntimeError("Number dapat ang operand bai.", operator.line)
    }

    private fun isTruthy(value: Any?): Boolean {
        return when (value) {
            null -> false
            is Boolean -> value
            else -> true
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
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
}
