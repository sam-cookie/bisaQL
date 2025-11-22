package scanner

fun main() {
    while (true) {
        println("Scanner Test - Enter input: ")
        val source = readLine() ?: ""

        if (source.equals("")) {
            println("Empty input")
        } else if (source.equals("humana")) break
        

        val scanner = Scanner(source) 
        val tokens = scanner.scanTokens()
            
        println("Tokens:")
        for (token in tokens) {
            println("  $token")
        }
    }
}