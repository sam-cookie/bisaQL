package main

import scanner.Scanner
import parser.Parser
import Evaluator.Evaluator

fun main() {
    println("Welcome to HiliSaya Interpreter!")
    println("Type 'humana' to exit.")
    
    val evaluator = Evaluator()

    while (true) {
        val input = readMultiLineInput() ?: break
        if (input.isEmpty()) continue


        val scanner = Scanner(input)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)
        val program = parser.parseProgram()

        evaluator.executeProgram(program)
        // } catch (e: Exception) {
        //     println("Error: ${e.message}")
        // }
    }
}

private fun readMultiLineInput(): String? {
    print("> ")
    var input = readLine() ?: return null
    var trimmed = input.trim()
    
    if (trimmed.isEmpty()) return ""
    if (trimmed.lowercase() == "humana") return null

    // Handle multi-line blocks with nested block tracking
    if (isBlockStart(trimmed)) {
        var blockDepth = 1 // Start with 1 for the initial sugod
        
        while (blockDepth > 0) {
            // Create prompt based on current nesting level
            val prompt = "..".repeat(blockDepth) + " "
            print(prompt)
            
            val line = readLine() ?: break
            trimmed = line.trim()
            input += "\n" + line
            
            if (isBlockStart(trimmed)) {
                blockDepth++ // Entering a nested block
            } else if (isBlockEnd(trimmed)) {
                blockDepth-- // Exiting a block
            }
        }
        
        // If we broke out due to EOF but blocks are still open
        if (blockDepth > 0) {
            println("Wala na close ang blocks bai!")
        }
    }
    
    return input
}

private fun isBlockStart(input: String): Boolean {
    val clean = input.trim().lowercase()
    return clean == "sugod" || clean == "sugod."
}

private fun isBlockEnd(input: String): Boolean {
    val clean = input.trim().lowercase()
    return clean == "tapos" || clean == "tapos."
}