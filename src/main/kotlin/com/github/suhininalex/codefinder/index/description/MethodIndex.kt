package com.github.suhininalex.codefinder.index.description

import com.github.suhininalex.codefinder.preprocessing.MethodDescription

interface MethodIndex {
    fun indexMethod(method: MethodDescription)
    fun getMethodById(id: String): MethodDescription?
    fun getUsagesOf(id: String): Collection<String>
    fun useMethods(body: (Sequence<MethodDescription>)->Unit)
}