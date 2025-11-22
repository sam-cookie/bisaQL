package main

import scanner.Scanner
import parser.Parser
import Evaluator.Evaluator

fun main() {
    println("Welcome to HiliSaya Interpreter!")
    println("Type 'humana' to exit.")
    
    val evaluator = Evaluator()
    val inputHandler = InputHandler()

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