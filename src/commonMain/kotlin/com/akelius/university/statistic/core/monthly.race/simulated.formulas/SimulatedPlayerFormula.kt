package com.akelius.university.statistic.core.monthly.race.simulated.formulas

interface SimulatedPlayerFormula {
    companion object {
        const val maxScoreCalculatedOnUsers = 200 // Investigated in https://akelius.atlassian.net/browse/SCHOOLPF-3284
    }

    fun calculate(param: FormulaParameters): Int
}

data class FormulaParameters (
    val activeDaysMin: Int,
    val activeDaysMax: Int,
    val percentageRangeOfMaxScore: Range,
    val core : CoreParams
)

data class CoreParams (
    val totalCurrentUserScore: Int,
    val previousPeriodUserScore: Int,
    val currentDayInPeriod: Int,
    val lengthOfPeriod: Int,
    val randomizationSeed: Long)

data class Range (val min: Double, val max: Double)