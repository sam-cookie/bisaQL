package main
import errorhandling.HiliSayaError
import errorhandling.RuntimeError

class InputHandler {
    fun readMultiLineInput(): String? {
        while (true) { 
            print("> ")
            var input = readLine() ?: return null
            var trimmed = input.trim()
            var lineNumber = 1 // track line numbers

            if (trimmed.isEmpty()) return ""

            if (trimmed.lowercase() == "humana") return null

            try {
                if (isBlockStart(trimmed)) {
                    var blockDepth = 1

                    while (blockDepth > 0) {
                        val prompt = "".repeat(blockDepth) + " "
                        print(prompt)

                        val line = readLine() ?: break
                        input += "\n$line"
                        lineNumber++
                        val t = line.trim()

                        if (isBlockStart(t)) blockDepth++
                        if (isBlockEnd(t)) blockDepth--
                    }

                    if (blockDepth > 0) {
                        throw RuntimeError("Wala na close ang blocks bai!", lineNumber)
                    }
                }

                // handle /* */ comments
                else if (trimmed.startsWith("/*")) {
                    var blockDepth = 1

                    while (blockDepth > 0) {
                        val prompt = "> "
                        print(prompt)

                        val line = readLine() ?: break
                        input += "\n$line"
                        lineNumber++
                        val t = line.trim()

                        if (t.contains("/*")) blockDepth++
                        if (t.contains("*/")) blockDepth--
                    }

                    if (blockDepth > 0) {
                        throw RuntimeError("Wala na close ang block comment!", lineNumber)
                    }
                }

                return input
            } catch (e: RuntimeError) {
                println("Runtime Error: ${e.message}")
                // loop will restart and ask for input again
            }
        }
}


    private fun isBlockStart(input: String): Boolean {
        val trimmed = input.trim()

        val allowed = listOf("Sugod", "Samtang", "Kung", "Ugdi", "Buhatag")

        for (kw in allowed) {
            if (trimmed.startsWith(kw)) return true

            if (trimmed.lowercase().startsWith(kw.lowercase())) {
                throw RuntimeError("Dapat magsugod sa uppercase '$kw'.", 1)
            }
        }

        return false
    }

    private fun isBlockEnd(input: String): Boolean {
        val clean = input.trim().lowercase()
        return clean == "tapos" || clean == "tapos."
    }
}
