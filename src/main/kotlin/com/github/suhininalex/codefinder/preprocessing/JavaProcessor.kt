package com.github.suhininalex.codefinder.preprocessing

import com.github.javaparser.JavaParser
import com.github.suhininalex.codefinder.utils.*
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.suhininalex.codefinder.preprocessing.tokens.*
import java.io.File

class JavaProcessor(vararg solverPackages: String) {

    init {
        JavaParser.getStaticConfiguration().configureSolver(*solverPackages)
    }

    companion object {
        const val unresolved: String = "unresolved"
    }

    fun parse(file: File): FileDescription {
        val fileAst = JavaParser.parse(file)
        return parse(fileAst)
    }

    fun parse(code: String): FileDescription {
        val codeAst = JavaParser.parse(code)
        return parse(codeAst)
    }

    private fun parse(file: CompilationUnit): FileDescription {
        return with (file) {
            FileDescription(
                    packageName = packageDeclaration.map { it.nameAsString }.orElse(unresolved),
                    imports = imports.mapNotNull { it.packageName },
                    methods = findAll(MethodDeclaration::class.java).map { method -> parse(method) }
            )
        }
    }

    private fun parse(method: MethodDeclaration): MethodDescription {
        val resolvedDescription = method.resolve()
        return with(method) {
            MethodDescription(
                    qualifiedName = resolvedDescription.qualifiedName.replaceAfterLast(".", "$signature"),
                    className = resolvedDescription.className,
                    packageName = resolvedDescription.packageName,
                    declaration = declarationAsString,
                    javaDoc = javadoc.map { it.toText().asLine() }.orElse(""),
                    content = body.map { body -> parse(body) }.orElse(emptyList()),
                    rawContent = body.map { it.toString() }.orElse("")
            )
        }
    }

    private fun parse(body: BlockStmt): List<Token> {
        return body.elements().mapNotNull { node -> parse(node) }
    }

    private fun parse(node: Node): Token? {
        return when (node) {
            is SimpleName -> parse(node)
            is StringLiteralExpr -> LiteralToken(node.asString())
            is Comment -> CommentToken(node.content)
            else -> {
                node.comment
                    .map { CommentToken(it.content) }
                    .orElse(null)
            }
        }
    }

    private fun parse(identifier: SimpleName): Token {
        val parent = identifier.parentNode.orElseThrow { IllegalStateException("There is no parent for node $this") }
        return when (parent){
            is MethodCallExpr -> parse(parent)
            is ClassOrInterfaceType -> TypeToken(identifier.asString())
            else -> IdentifierToken(identifier.asString())
        }
    }

    private fun parse(methodCall: MethodCallExpr): CallToken {
        return CallToken(
                name = methodCall.nameAsString,
                reference = tryOrNull { MethodSolver.resolveInvokedMethod(methodCall)?.fullyQualifiedName }
                        ?: unresolved
        )
    }
}