package com.akelius.university.statistic.core.dto

data class RaceParameters(
    val totalCurrentUserScore: Int,
    val previousPeriodUserScore: Int,
    val currentDayInPeriod: Int,
    val lengthOfPeriod: Int,
    val randomizationSeed: RandomizationSeed
)

data class RandomizationSeed(
    val currentYear: Int,
    val currentMonth: Int,
    val userSpecificSeed: Int? = null
)