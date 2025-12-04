package evaluator

import scanner.Token
import parser.Stmt
import errorhandling.ReturnException
import errorhandling.RuntimeError

class FunctionObject(
    val name: Token,
    val params: List<Token>,
    val body: Stmt.Block,
    val closure: Environment 
    ) {
        // call the function with arguments
        fun call(args: List<Any?>): Any? {
            // arity check
            if (args.size != params.size) {
                throw RuntimeError(
                    "Ginaexpect ${params.size} ka arguments, pero kani ang nakuha: ${args.size}.",
                    name.line
                )
            }

            val localEnv = Environment(closure)

            // assign arguments to parameters
            params.forEachIndexed { i, param ->
                localEnv.define(param.lexeme, args[i])
            }

            try {
                Evaluator(localEnv).executeBlock(body.statements, localEnv)
            } catch (ret: ReturnException) {
                return ret.value
            }

            return null
        }

}
