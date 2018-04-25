package com.github.suhininalex.codefinder.preprocessing

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseProblemException
import com.github.suhininalex.codefinder.utils.findFilesByType
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class PackageMaker(private val outputDir: String) {

    fun processFiles(path: String){
        val inputDir = File(path)
        inputDir.findFilesByType("java").forEach { file ->
            packageFile(file)
        }
    }

    private fun packageFile(file: File){
        val packagePath = getPackageName(file).replace(".", "/")
        val outFile = File("$outputDir/$packagePath/${file.name}")
        outFile.mkdirs()
        Files.copy(file.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun getPackageName(file: File): String{
        return try {
            JavaParser.parse(file).packageDeclaration.map { it.nameAsString }.orElse("unresolved") }
        catch (e: ParseProblemException) {
            JavaProcessor.unresolved
        }
    }
}