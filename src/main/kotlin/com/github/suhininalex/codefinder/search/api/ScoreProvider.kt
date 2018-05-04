package com.github.suhininalex.codefinder.search.api

interface ScoreProvider<in T> {
    fun score(element: T): Score
}