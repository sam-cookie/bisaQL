package evaluator

import parser.*
import scanner.*
import errorhandling.RuntimeError

class Environment(val enclosing: Environment? = null) {
    private val values = mutableMapOf<String, Any?>()

    // define a variable (can be null)
    fun define(name: String, value: Any? = null) {
        values[name] = value
    }

    // assign a value to an existing variable
    fun assign(name: String, value: Any?, line: Int): Any? {
        if (values.containsKey(name)) {
            values[name] = value
            return value
        }
        return enclosing?.assign(name, value, line)
            ?: throw RuntimeError("Wa manay variable '$name' bai.", line)
    }

    // get the value of a variable
    fun get(name: String, line: Int): Any? {
        if (values.containsKey(name)) return values[name] // can be null
        return enclosing?.get(name, line)
            ?: throw RuntimeError("Wa ma-define ang variable '$name' bai.", line)
    }
}
