package parser
import scanner.*

sealed class Stmt {
    data class Program(val statements: List<Stmt>) : Stmt()
    data class Var(val name: Token, val initializer: Expr?) : Stmt()
    data class Assign(val name: Token, val value: Expr) : Stmt()
    data class Print(val expression: Expr) : Stmt()
    data class ExprStmt(val expression: Expr) : Stmt()
    data class Block(val statements: List<Stmt>) : Stmt()
    data class Fun( val name: Token, val params: List<Token>, val body: Stmt.Block) : Stmt()
    data class Call(val name: Token, val arguments: List<Expr>) : Stmt()
    data class While(val condition: Expr, val statements: List<Stmt>) : Stmt()
    data class If(val condition: Expr, val statements: List<Stmt>, val elseBranch: Stmt?) : Stmt()
}