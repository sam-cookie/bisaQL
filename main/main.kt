package main

import java.io.File
import scanner.Scanner
import parser.Parser
import evaluator.Evaluator 

fun main(args: Array<String>) {

    val evaluator = Evaluator(isReplMode = args.isEmpty())
    val inputHandler = InputHandler()

    if (args.isNotEmpty()) {
        val path = args[0]

        try {
            val source = File(path).readText()

            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            evaluator.executeProgram(program)

        } catch (e: Exception) {
            println("[Runtime error] Di mabasa ang file: $path")
        }

        return
    } else {

        println("Welcome to HiliSaya Interpreter!")
        println("Type 'humana' to exit.")

        while (true) {
            val input = inputHandler.readMultiLineInput() ?: break
            if (input.isEmpty()) continue

            val scanner = Scanner(input)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            evaluator.executeProgram(program)
        }
    }
}
