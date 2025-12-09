package evaluator

import parser.*
import scanner.*
import errorhandling.RuntimeError
import errorhandling.ReturnException

class Evaluator(private var environment: Environment = Environment()) {

    init {
        // register native function
        environment.define("orasSubong", TimeFunc())
        environment.define("petsaSubong", DateFunc())
        environment.define("katason", KatasonFunc())
        environment.define("letter", LetterFunc())
    }

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

            is Stmt.While -> {
                while (evaluate(stmt.condition) as? Boolean == true) {
                    executeBlock(stmt.statements, Environment(environment))
                }
            }

            is Stmt.For -> {
                evaluate(stmt.initializer)
                while (evaluate(stmt.condition) as? Boolean == true) {
                    executeBlock(stmt.body, Environment(environment))
                    execute(stmt.update)
                }
            }

            is Stmt.If -> {
                if (isTruthy(evaluate(stmt.condition))) {
                    executeBlock(stmt.statements, Environment(environment))
                } else if (stmt.elseBranch != null) {
                    executeBlock((stmt.elseBranch as Stmt.Block).statements, Environment(environment))
                }
            }

            is Stmt.Fun -> {
                val functionObj = FunctionObject(stmt.name, stmt.params, stmt.body, environment)
                environment.define(stmt.name.lexeme, functionObj)
            }

            is Stmt.Call -> {
                // evaluate call as an expression to get return value
                evaluate(Expr.Call(Expr.Variable(stmt.name), stmt.name, stmt.arguments))

            }
            

            is Stmt.Return -> {
                val value = stmt.value?.let { evaluate(it) }
                throw ReturnException(value)
            }
        }
    }


    fun executeBlock(statements: List<Stmt>, blockEnv: Environment) {
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
            is Expr.Call -> {
                val calleeValue = evaluate(expr.callee)

                val args = expr.arguments.map { evaluate(it) }

                return when (calleeValue) {
                    is FunctionObject -> calleeValue.call(args)
                    is NativeFunction -> {
                        if (args.size != calleeValue.arity) {
                            throw RuntimeError(
                                "Expected ${calleeValue.arity} arguments but got ${args.size}.",
                                (expr.callee as? Expr.Variable)?.name?.line ?: 0
                            )
                        }
                        calleeValue.call(this, args)
                    }
                    else -> throw RuntimeError(
                        "${(expr.callee as? Expr.Variable)?.name?.lexeme ?: "unknown"} dili function bai.",
                        (expr.callee as? Expr.Variable)?.name?.line ?: 0
                    )
                }
            }
        }
    }

    private fun evaluateUnary(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        return when (expr.operator.type) {
            TokenType.MINUS -> right.toNumber(expr.operator)
            TokenType.NOT -> !isTruthy(right)
            TokenType.LENGTH -> {
                if (right !is String)
                throw RuntimeError("Ang pag kay para ra sa string!", expr.operator.line)
                // return (expr.right as String).length.toDouble()
                right.length.toDouble()
            }
            else -> throw RuntimeError("Unsupported unary operator.", expr.operator.line)
        }
    }

    private fun evaluateBinary(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.PLUS -> left.toNumber(expr.operator) + right.toNumber(expr.operator)
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
            TokenType.SUMPAY -> {
                if (left is String || right is String) valueToString(left) + valueToString(right)
                else throw RuntimeError("Mali nga operands for sumpayig bai!", expr.operator.line)
            }

            TokenType.CHAR -> {
                if (left !is String) throw RuntimeError("Dili man 'string' ang nasa kaliwa bai.", expr.operator.line)

                val index = when (right) {
                    is Number -> right.toInt()
                    else -> throw RuntimeError("Dili man 'number' ang nasa tuo bai.", expr.operator.line)
                }

                if (index < 0 || index >= left.length)
                    throw RuntimeError("Index $index out of range for string '$left'.", expr.operator.line)

                return left[index].toString()
            }

            TokenType.LENGTH -> {
                // left must be string
                if (left !is String)
                    throw RuntimeError("String dapat ang kuhaan ug 'katason'.", expr.operator.line)

                return left.length.toDouble()
            }
            
            TokenType.GREATER_THAN -> left.compareNumbers(right, expr.operator) { a, b -> a > b }
            TokenType.GREATER_THAN_EQUAL -> left.compareNumbers(right, expr.operator) { a, b -> a >= b }
            TokenType.LESS_THAN -> left.compareNumbers(right, expr.operator) { a, b -> a < b }
            TokenType.LESS_THAN_EQUAL -> left.compareNumbers(right, expr.operator) { a, b -> a <= b }
            TokenType.EQUALTO -> left == right
            TokenType.NOT_EQUAL -> left != right
            TokenType.AND -> isTruthy(left) && isTruthy(right)
            TokenType.OR -> isTruthy(left) || isTruthy(right)
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

    private fun executeCall(stmt: Stmt.Call): Any? {
        val calleeValue = environment.get(stmt.name.lexeme, stmt.name.line)
        if (calleeValue !is FunctionObject) throw RuntimeError("${stmt.name.lexeme} dili function bai.", stmt.name.line)
        val func = calleeValue
        val args = stmt.arguments.map { evaluate(it) }
        return func.call(args)  // ✅ now returns function value
    }

}
