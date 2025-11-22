package parser
import scanner.*

fun main() {
    println(" HiliSaya Parser Test ")

    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        if (line.trim() == "humana") break 
        if (line.isBlank()) continue

        try {
            val scanner = Scanner(line)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val program = parser.parseProgram()

            val printer = AstPrinter()
            for (stmt in program.statements) { 
                printer.printStmt(stmt)
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}