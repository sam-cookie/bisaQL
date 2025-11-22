package main

class InputHandler {
    fun readMultiLineInput(): String? {
        print("> ")
        var input = readLine() ?: return null
        var trimmed = input.trim()
        
        if (trimmed.isEmpty()) return ""
        if (trimmed.lowercase() == "humana") return null

        if (isBlockStart(trimmed)) {
            var blockDepth = 1
            
            while (blockDepth > 0) {
                val prompt = "..".repeat(blockDepth) + " "
                print(prompt)
                
                val line = readLine() ?: break
                trimmed = line.trim()
                input += "\n" + line
                
                if (isBlockStart(trimmed)) {
                    blockDepth++
                } else if (isBlockEnd(trimmed)) {
                    blockDepth--
                }
            }
            
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
}