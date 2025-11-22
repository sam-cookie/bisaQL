package Evaluator
import parser.*
import scanner.*

fun main() {
    // for evaluations
    val evaluator = Evaluator(isReplMode = true)

    println("Hilisaya Programming Language - REPL Mode")
    println("Type 'humana' to exit.")

    while (true) {
        print("> ")
        val line = readLine() ?: break
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue
        if (trimmed.lowercase() == "humana") break

        try {
            // Scan tokens
            val scanner = Scanner(line)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            // Execute each statement
            evaluator.executeProgram(program)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}