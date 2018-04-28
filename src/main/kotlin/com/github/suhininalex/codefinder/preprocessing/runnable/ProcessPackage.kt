package com.github.suhininalex.codefinder.preprocessing.runnable

import com.github.javaparser.JavaParser
import com.github.suhininalex.codefinder.preprocessing.JavaProcessor
import com.github.suhininalex.codefinder.preprocessing.tokens.TokenJsonSerializer
import com.github.suhininalex.codefinder.utils.findFilesByType
import java.io.File

fun main(args: Array<String>) {
    require(args.size == 2) { "Input(package)/output directory is not defined" }

    val (inputPackage, output) = args

    PackageProcessor(inputPackage, output).run()
}

class PackageProcessor(private val inputPackage: String, private val outputDirectory: String){

    private val processor = JavaProcessor(inputPackage)

    private fun processFile(file: File){
        val fileAst = JavaParser.parse(file)
        val fileDescription = processor.parse(fileAst) //Error
        val packageName = fileAst.packageDeclaration.map { it.nameAsString }.orElse(JavaProcessor.unresolved)
        val packagePath = packageName.replace(".", "/")
        val outputFile = File("$outputDirectory/$packagePath/${file.nameWithoutExtension}.json")
        outputFile.parentFile.mkdirs()
        outputFile.bufferedWriter().use { output ->
            TokenJsonSerializer.toJson(fileDescription, output)
        }
    }

    fun run(){
        val files = File(inputPackage).findFilesByType("java").toList()
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
}