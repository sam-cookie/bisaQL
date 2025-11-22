package parser
import scanner.*

sealed class Stmt {
    data class Program(val statements: Array<Stmt>) : Stmt()
    data class Var(val name: Token, val initializer: Expr?) : Stmt()
    data class Assign(val name: Token, val value: Expr) : Stmt()
    data class Print(val expression: Expr) : Stmt()
    data class ExprStmt(val expression: Expr) : Stmt()
    data class Block(val statements: Array<Stmt>) : Stmt()
}