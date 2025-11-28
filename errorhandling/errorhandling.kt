package errorhandling

open class HiliSayaError(
    message: String,
    val line: Int
) : RuntimeException(message)
