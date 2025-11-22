package scanner

fun main() {
    while (true) {
        println("Scanner Test - Enter input: ")
        val source = readLine() ?: ""

        if (source.equals("")) {
            println("Empty input")
        } else if (source.equals("humana")) break
        
        try {
            val scanner = Scanner(source) // Remove extra parameters
            val tokens = scanner.scanTokens()
            
            println("Tokens:")
            for (token in tokens) {
                println("  $token")
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}