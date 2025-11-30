package evaluator

import parser.*
import scanner.*
import errorhandling.RuntimeError

class Evaluator(private var environment: Environment = Environment()) {

    fun executeProgram(program: Stmt.Program) {
        program.statements.forEach { execute(it) }
    }

    fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Var -> {
                val value = stmt.initializer?.let { evaluate(it) }
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

            is Stmt.ExprStmt -> evaluate(stmt.expression)

            is Stmt.Block -> executeBlock(stmt.statements, Environment(environment))

            is Stmt.Program -> executeProgram(stmt)
        }
    }

    private fun executeBlock(statements: List<Stmt>, blockEnv: Environment) {
        val previous = environment
        environment = blockEnv
        try {
            statements.forEach { execute(it) }
        } finally {
            environment = previous
        }
    }

    fun evaluate(expr: Expr?): Any? {
        return when (expr) {
            null -> null
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
            TokenType.MINUS -> right.toNumber(expr.operator)
            TokenType.NOT -> !isTruthy(right)
            else -> throw RuntimeError("Unsupported unary operator.", expr.operator.line)
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            // Arithmetic
            TokenType.PLUS -> if (left is Number && right is Number) left.toDouble() + right.toDouble()
                              else valueToString(left) + valueToString(right)

            TokenType.MINUS -> left.toNumber(expr.operator) - right.toNumber(expr.operator)
            TokenType.TIMES -> left.toNumber(expr.operator) * right.toNumber(expr.operator)
            
            TokenType.DIVIDE -> {
                val r = right.toNumber(expr.operator)
                if (r == 0.0) throw RuntimeError("Bawal magdivide by 0 bai.", expr.operator.line)
                left.toNumber(expr.operator) / r
            }
            
            TokenType.MODULO -> {
                val r = right.toNumber(expr.operator)
                if (r == 0.0) throw RuntimeError("Bawal mag modulo by 0 bai.", expr.operator.line)
                left.toNumber(expr.operator) % r
            }
            
            TokenType.SUMPAY -> valueToString(left) + valueToString(right)

            // Comparison
            TokenType.GREATER_THAN -> left.compareNumbers(right, expr.operator) { a, b -> a > b }
            TokenType.GREATER_THAN_EQUAL -> left.compareNumbers(right, expr.operator) { a, b -> a >= b }
            TokenType.LESS_THAN -> left.compareNumbers(right, expr.operator) { a, b -> a < b }
            TokenType.LESS_THAN_EQUAL -> left.compareNumbers(right, expr.operator) { a, b -> a <= b }

            TokenType.EQUALTO -> left == right       // parehas
            TokenType.NOT_EQUAL -> left != right     // dili parehas

            TokenType.AND -> isTruthy(left) && isTruthy(right) // ug
            TokenType.OR -> isTruthy(left) || isTruthy(right)  // o

            else -> throw RuntimeError("Unsupported binary operator: ${expr.operator.lexeme}", expr.operator.line)
        }
    }

    // --- helpers ---
    private fun Any?.toNumber(operator: Token): Double = when (this) {
        is Number -> this.toDouble()
        else -> throw RuntimeError("Number dapat ang operand bai.", operator.line)
    }

    private fun Any?.compareNumbers(right: Any?, operator: Token, comp: (Double, Double) -> Boolean): Boolean {
        return this.toNumber(operator).let { leftNum -> comp(leftNum, right.toNumber(operator)) }
    }

    private fun isTruthy(value: Any?): Boolean = when (value) {
        null -> false
        is Boolean -> value
        else -> true
    }

    private fun valueToString(value: Any?): String = when (value) {
        null -> "wala" 
        true -> "tuod"
        false -> "atik"
        is Double -> if (value % 1 == 0.0) value.toInt().toString() else value.toString()
        is String -> value 
        else -> value.toString()
    }
}