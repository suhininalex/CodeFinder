package com.github.suhininalex.codefinder.preprocessing.runnable

import com.github.javaparser.JavaParser
import com.github.suhininalex.codefinder.preprocessing.JavaProcessor
import com.github.suhininalex.codefinder.preprocessing.tokens.TokenJsonSerializer
import com.github.suhininalex.codefinder.utils.findFilesByType
import com.github.suhininalex.codefinder.utils.wrapWithProgressBar
import mu.KotlinLogging
import java.io.File

fun main(args: Array<String>) {
    require(args.size == 2) { "Input(package)/output directory is not defined" }

    val (inputPackage, output) = args

    PackageProcessor(inputPackage, output).run()
}

class PackageProcessor(private val inputPackage: String, private val outputDirectory: String){

    private val processor = JavaProcessor(inputPackage)

    private val logger = KotlinLogging.logger {  }

    private fun processFile(file: File){
        val fileAst = JavaParser.parse(file)
        val fileDescription = processor.parse(file) //Error
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
        files.wrapWithProgressBar("Processing java package").forEach { file ->
            try {
                processFile(file)
            } catch (e: Exception){
                logger.warn { "Could not parse file ${file.name}" }
                logger.trace { e }
            }
        }
    }
}