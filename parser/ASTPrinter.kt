package parser

import scanner.*

// AST printer
class AstPrinter {

    fun print(expr: Expr) {
        println(astToString(expr))
    }

    private fun astToString(expr: Expr): String = when (expr) {
        is Expr.Binary -> {
            val op = when (expr.operator.type) {
                TokenType.PLUS -> "idugang sa"
                TokenType.MINUS -> "ibawas sa"
                TokenType.TIMES -> "itimes sa"
                TokenType.DIVIDE -> "idivide sa"
                TokenType.MODULO -> "modulo"
                TokenType.GREATER_THAN -> "mas dako sa"
                TokenType.GREATER_THAN_EQUAL -> "mas dako or pareha sa"
                TokenType.LESS_THAN -> "mas gamay sa"
                TokenType.LESS_THAN_EQUAL -> "mas gamay or pareha sa"
                TokenType.EQUALTO -> "kay pareha sa"
                TokenType.NOT_EQUAL -> "dili pareha sa"
                TokenType.AND -> "ug"
                TokenType.OR -> "or"
                TokenType.SUMPAY -> "sumpayig"
                else -> expr.operator.lexeme
            }
            "${astToString(expr.left)} $op ${astToString(expr.right)}"
        }

        is Expr.Unary -> {
            val op = when (expr.operator.type) {
                TokenType.NOT -> "dele"
                TokenType.MINUS -> "negatib!"
                TokenType.PLUS -> "pasitib"
                else -> expr.operator.lexeme
            }
            "$op ${astToString(expr.right)}"
        }

        is Expr.Literal -> when (expr.value) {
            null -> "wala"
            is String -> expr.value
            else -> expr.value.toString()
        }

        is Expr.Grouping -> "( ${astToString(expr.expression)} )"
        is Expr.Variable -> expr.name.lexeme
        is Expr.Assign -> "(${expr.name.lexeme} = ${astToString(expr.value)})"

        is Expr.Call -> {
            val args = expr.arguments.joinToString(", ") { astToString(it) }
            val calleeName = when (val callee = expr.callee) {
                is Expr.Variable -> callee.name.lexeme
                else -> astToString(callee)
            }
            "$calleeName($args)"
        }
    }

    fun printStmt(stmt: Stmt) {
        println(stmtToString(stmt))
    }

    private fun stmtToString(stmt: Stmt, indent: String = ""): String = when (stmt) {
        is Stmt.Program -> stmt.statements.joinToString("\n") { stmtToString(it, indent) }
        is Stmt.Var -> indent + "bar ${stmt.name.lexeme}" +
                (stmt.initializer?.let { " = ${astToString(it)}" } ?: "")
        is Stmt.Assign -> indent + "${stmt.name.lexeme} = ${astToString(stmt.value)}"
        is Stmt.ExprStmt -> indent + astToString(stmt.expression)
        is Stmt.Print -> indent + "gawas ${astToString(stmt.expression)}"
        is Stmt.Block -> {
            "Sugod\n" +
            stmt.statements.joinToString("\n") { stmtToString(it, indent + "    ") } +
            "\n${indent}Tapos"
        }
        is Stmt.While -> {
            "Samtang ang ${astToString(stmt.condition)} buhata,\n" +
            stmt.statements.joinToString("\n") { stmtToString(it, indent + "    ") } +
            "\n${indent}tapos."
        }
        is Stmt.If -> {
            val blockStmt = stmt.statements.joinToString("\n") { stmtToString(it, indent + "    ") }
            val elseBlockStmt = stmt.elseBranch?.let { 
                "\nUgdi\n" + (it as Stmt.Block).statements.joinToString("\n") { stmtToString(it, indent + "    ") }
            } ?: ""
            "Kung ang ${astToString(stmt.condition)} buhata,\n$blockStmt$elseBlockStmt\n${indent}tapos."
        }
        is Stmt.Fun -> {
            val params = stmt.params.joinToString(", ") { it.lexeme }
            indent + "Buhatag ${stmt.name.lexeme}($params)\n" +
                stmt.body.statements.joinToString("\n") { stmtToString(it, indent + "    ") } +
                "\n${indent}Tapos"
        }
        is Stmt.Call -> {
            val args = stmt.arguments.joinToString(", ") { astToString(it) }
            indent + "${stmt.name.lexeme}($args)"
        }
    }
}
