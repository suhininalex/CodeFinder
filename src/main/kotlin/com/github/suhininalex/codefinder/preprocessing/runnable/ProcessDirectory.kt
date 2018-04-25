package com.github.suhininalex.codefinder.preprocessing.runnable

import com.github.javaparser.JavaParser
import com.github.suhininalex.codefinder.preprocessing.JavaProcessor
import com.github.suhininalex.codefinder.preprocessing.TokenJsonSerializer
import com.github.suhininalex.codefinder.preprocessing.configureSolver
import com.github.suhininalex.codefinder.utils.findFilesByType
import java.io.File

fun main(args: Array<String>) {
    require(args.size == 2) { "Input(package)/output directory is not defined" }

    val (inputPackage, output) = args
    val processor = JavaProcessor()
    val files = File(inputPackage).findFilesByType("java").toList()
    JavaParser.getStaticConfiguration().configureSolver(inputPackage)

    fun processFile(file: File){
        val fileAst = JavaParser.parse(file)
        val fileDescription = processor.parse(fileAst)
        val packageName = fileAst.packageDeclaration.map { it.nameAsString }.orElse(JavaProcessor.unresolved)
        val packagePath = packageName.replace(".", "/")
        val outputFile = File("$output/$packagePath/${file.nameWithoutExtension}.json")
        outputFile.parentFile.mkdirs()
        outputFile.bufferedWriter().use { output ->
            TokenJsonSerializer.toJson(fileDescription, output)
        }
    }

    files.forEachIndexed{ i, file ->
        try {
            println("$i/${files.size} Processing file: ${file.name}")
            processFile(file)
        } catch (e: Exception){
            println("Could not parse file ${file.name}")
            e.printStackTrace()
        }
    }

}