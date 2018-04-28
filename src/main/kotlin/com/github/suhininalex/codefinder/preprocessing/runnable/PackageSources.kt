package com.github.suhininalex.codefinder.preprocessing.runnable

import com.github.suhininalex.codefinder.preprocessing.PackageMaker

fun main(args: Array<String>) {
    require(args.size == 2) {"Input/output directory is not defined"}
    val (inputDirectory, outputDirectory) = args
    val packageMaker = PackageMaker(outputDirectory)
    packageMaker.processFiles(inputDirectory)
}