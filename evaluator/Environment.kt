package evaluator
import parser.*
import scanner.*

class Environment(val enclosing: Environment? = null) {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun assign(name: String, value: Any?): Any? {
        return if (values.containsKey(name)) {
            values[name] = value
            value
        } else enclosing?.assign(name, value) ?: run {
            println("[Runtime error] Wa manay variable '$name' bai.")
            null
        }
    }

    fun get(name: String): Any? {
        return if (values.containsKey(name)) values[name]
        else enclosing?.get(name) ?: run {
            println("[Runtime error] Wa ma-define nga variable '$name' bai.")
            null
        }
    }
}
