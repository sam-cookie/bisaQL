package evaluator

interface NativeFunction {
    val arity: Int
    fun call(evaluator: Evaluator, arguments: List<Any?>): Any?
}