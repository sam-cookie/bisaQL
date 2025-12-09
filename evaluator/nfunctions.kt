package evaluator
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import errorhandling.RuntimeError

class TimeFunc : NativeFunction {
    override val arity: Int = 0

    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any {
        val currentTime = java.time.LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }
}

class DateFunc : NativeFunction {
    override val arity: Int = 0

    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any {
        val currentDate = java.time.LocalDate.now()
        return currentDate.toString()  
    }
}

class LetterFunc : NativeFunction {
    override val arity = 2

    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any? {
        if (arguments.size != arity)
            throw RuntimeError("letter expects 2 arguments, got ${arguments.size}.", 0)

        val str = arguments[0] as? String ?: throw RuntimeError("First argument to letter must be a string.", 0)
        val index = when (val arg = arguments[1]) {
            is Number -> arg.toInt()
            else -> throw RuntimeError("Second argument to letter must be a number.", 0)
        }

        if (index < 0 || index >= str.length)
            throw RuntimeError("Index $index out of range for string '$str'.", 0)

        return str[index].toString()
    }
}

class KatasonFunc : NativeFunction {
    override val arity = 1

    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any? {
        if (arguments.size != arity)
            throw RuntimeError("katason expects 1 argument, got ${arguments.size}.", 0)

        val str = arguments[0] as? String ?: throw RuntimeError("Argument to katason must be a string.", 0)
        return str.length.toDouble()  // Your evaluator seems to handle numbers as Double
    }
}
