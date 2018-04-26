package com.github.suhininalex.codefinder.utils

import com.google.gson.Gson
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors
import java.util.stream.Stream


fun File.findFilesByType(extension: String): Sequence<File> {
    return walkTopDown()
            .filter { it.isFile }
            .filter { it.extension.toLowerCase() == extension }
}

fun <T> Sequence<T>.onEachIndexed(operation: (Int, T) -> Unit): Sequence<T> {
    return mapIndexed { i, value ->
        operation(i, value)
        value
    }
}

fun <R> tryOrNull(body: () -> R): R? = try { body() } catch (e: Exception){ null }

fun String.asLine(): String = replace(Regex("\\r?\\n"), " ").trim()

fun <T> Stream<T>.asSequence(): Sequence<T> = iterator().asSequence()

fun File.copyTo(destination: String){
    val target = File("$destination/$name")
    target.mkdirs()
    Files.copy(toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
}

fun <T> List<Set<T>>.intersection(): Set<T> {
    return drop(1).fold(firstOrNull().orEmpty()) {intersection, set -> intersection.intersect(set)}
}

fun <T> List<Set<T>>.unite(): Set<T> {
    return drop(1).fold(firstOrNull().orEmpty()) {union, set -> union.union(set)}
}

infix fun IntRange.clip(range: IntRange): IntRange {
    val begin = if (start > range.start) start else range.start
    val end = if (endInclusive < range.endInclusive) endInclusive else range.endInclusive
    return begin..end
}

infix fun IntRange.expand(size: Int): IntRange {
    return start-size..endInclusive+size
}

fun <T> List<T>.slice(range: IntRange): List<T> {
    if (isEmpty()) {
        return emptyList()
    } else {
        val indices = range clip indices
        return subList(indices.start, indices.endInclusive + 1)
    }
}

fun <T> List<T>.findPositions(condition: (T)->Boolean): List<Int> {
    return mapIndexedNotNull{ i, value -> if (condition(value)) i else null }
}

fun <R> withTime(name: String, body: ()->R): R{
    print("$name...")
    val start = System.currentTimeMillis()
    val result = body()
    val time = System.currentTimeMillis() - start
    println("($time ms)")
    return result
}

fun <T> Stream<T>.toList(): List<T> {
    return collect(Collectors.toList())
}

fun Gson.saveJson(filename: String, obj: Any){
    File(filename).writer().use { out ->
        toJson(obj, out)
    }
}

fun <T> List<T>.repeat(n: Int): List<T> {
    return (1..n).flatMap { this }
}

fun <T> Map<String, T>.find(key: String): List<String> {
    return keys.filter { key in it }
}

fun Collection<Double>.mean(): Double{
    val total = fold(1.0) {total, d -> total * d }
    return Math.pow(total, 1.0/size)
//    return this.average()
//    return size/map { 1/it }.sum()
}

fun <T> HashSet<T>?.orEmpty(): HashSet<T> {
    return this ?: HashSet<T>()
}

fun normalize(text: String): String {
    return text.trimIndent().replace("\n", "\r\n")
}