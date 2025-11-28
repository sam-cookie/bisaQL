package errorhandling

class RuntimeError(message: String, line: Int)
    : HiliSayaError(message, line)
