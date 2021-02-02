package com.akelius.university.statistic.core.dto

data class SlideshowScore(
    val slideScores: List<SlideScore>,
    val type: CalculationType = CalculationType.DEFAULT
)