package evaluator
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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