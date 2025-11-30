package main

import java.io.File
import scanner.Scanner
import parser.Parser
import evaluator.Evaluator
import errorhandling.RuntimeError

fun main(args: Array<String>) {

    val evaluator = Evaluator()
    val inputHandler = InputHandler()

    // Run from file if argument is provided
    if (args.isNotEmpty()) {
        val path = args[0]
        try {
            val source = File(path).readText()
            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            evaluator.executeProgram(program)

        } catch (e: RuntimeError) {
            println("[Line ${e.line}] Runtime Error: ${e.message}")
        } catch (e: Exception) {
            println("[Wa naexpect na error] ${e.message}")
        }
        return
    }


    // println("HiliSaya REPL - Enter commands (Ctrl+C to exit)")
    while (true) {
        // print("> ")
        val input = inputHandler.readMultiLineInput() ?: break
        if (input.isBlank()) continue

        try {
            val scanner = Scanner(input)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram() 

            evaluator.executeProgram(program)

        } catch (e: RuntimeError) {
            println("[Line ${e.line}] Runtime Error: ${e.message}")
        } catch (e: Exception) {
            println("[Wa naexpect na error] ${e.message}")
        }
    }
}
