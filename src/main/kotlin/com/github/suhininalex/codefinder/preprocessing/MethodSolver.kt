package com.github.suhininalex.codefinder.preprocessing

import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object MethodSolver {
    private val executor = Executors.newSingleThreadExecutor()
    private const val timeout = 50L

    fun resolveInvokedMethod(method: MethodCallExpr): ResolvedMethodDeclaration? {
        val task = executor.submit(Callable{ method.resolveInvokedMethod() })

        try {
            return task.get(timeout, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException){
            task.cancel(true)
            return null
        }
    }
}