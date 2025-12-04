// package evaluator

// import scanner.Token
// import errorhandling.RuntimeError

// class NativeFunction(
//     val name: String,
//     val arity: Int,
//     val implementation: (List<Any?>) -> Any?
// ) {
//     fun call(args: List<Any?>): Any? {
//         if (args.size != arity) {
//             throw RuntimeError(
//                 "Ginaexpect $arity ka arguments sa $name, pero ${args.size} ra ang nakuha.",
//                 0
//             )
//         }
//         return implementation(args)
//     }

//     override fun toString(): String = "<native fn $name>"
// }
