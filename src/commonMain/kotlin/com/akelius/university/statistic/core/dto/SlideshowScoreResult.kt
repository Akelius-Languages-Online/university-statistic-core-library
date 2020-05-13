package com.akelius.university.statistic.core.dto

data class SlideshowScoreResult(
    val score: Int,
    val scaledScore: Double,
    val correctAnswersCount: Int,
    val totalAnswersCount: Int
)