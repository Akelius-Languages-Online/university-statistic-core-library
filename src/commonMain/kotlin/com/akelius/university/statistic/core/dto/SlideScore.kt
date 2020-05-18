package com.akelius.university.statistic.core.dto

data class SlideScore(
    val isCorrect: Boolean,
    val score: Double? = null,
    val maxScore: Double = 1.0,
    val weight:  Double = 1.0
) 