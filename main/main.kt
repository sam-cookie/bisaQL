package main

import scanner.Scanner
import parser.Parser
import Evaluator.Evaluator

fun main() {
    println("Welcome to HiliSaya Interpreter!")
    // println("Syntax:")
    // println("  Single: bar x = 10.")
    // println("  Single: gawas x.")
    // println("  Block:  sugod")
    // println("            bar x = 10.")
    // println("            gawas x.")
    // println("          tapos.")
    // println("Type 'humana' to exit.")
    
    val evaluator = Evaluator()

    while (true) {
        print("> ")
        var input = readLine() ?: break
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) continue
        if (trimmed.lowercase() == "humana") break

        // Handle multi-line blocks
        if (trimmed.lowercase() == "sugod" || trimmed.lowercase() == "sugod.") {
            while (true) {
                print(".. ")
                val line = readLine() ?: break
                val lineTrimmed = line.trim()
                input += "\n" + line
                
                // Check if we reached the end of the block
                if (lineTrimmed.lowercase() == "tapos.") {
                    break
                }
            }
        }

        try {
            val scanner = Scanner(input)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            evaluator.executeProgram(program)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}