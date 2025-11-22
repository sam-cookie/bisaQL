package Evaluator
import parser.*
import scanner.*

class EvaluatorPrinter {

    // formats the evaluation result for printing
    fun printResult(value: Any?) {
        val output = when (value) {
            null -> "waay"
            true -> "tuod"
            false -> "atik"
            is Double -> {
                if (value % 1 == 0.0) value.toInt().toString() 
                else value.toString()
            }
            else -> value.toString()
        }

        println(output)
    }
}
