package com.github.suhininalex.codefinder.preprocessing

import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.Node
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import com.github.suhininalex.codefinder.utils.toList
import java.io.File

val Node.firstChild: Node?
    get() = childNodes.firstOrNull()

val ImportDeclaration.packageName: String?
    get() {
        val name = when {
            isStatic && isAsterisk -> name.firstChild
            isStatic -> name.firstChild?.firstChild
            isAsterisk -> name
            else -> name.firstChild
        }
        return name?.toString()
    }

val ResolvedMethodDeclaration.fullyQualifiedName: String
    get() = qualifiedName.replaceAfterLast(".", signature)

fun ParserConfiguration.configureSolver(vararg path: String){
    val solvers = path.map { JavaParserTypeSolver(File(it)) } + ReflectionTypeSolver()
    val combinedSolver = CombinedTypeSolver().apply {
        solvers.forEach { add(it) }
    }
    setSymbolResolver(JavaSymbolSolver(combinedSolver))
}

fun Node.elements(): List<Node> {
    return stream().toList()
}

val CompilationUnit.packagePath: String
    get() {
        return packageDeclaration
                .map { it.nameAsString }.orElse(JavaProcessor.unresolved)
                .replace(".", "/")
    }
