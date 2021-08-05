package com.akelius.university.statistic.core.monthly.race.simulated.formulas.options

import com.akelius.university.statistic.core.monthly.race.simulated.formulas.CoreParams
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.FormulaParameters
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.Range
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.SimulatedPlayerFormula.Companion.maxScoreCalculatedOnUsers
import kotlin.random.Random
import kotlin.test.*

class AlwaysFirstFormulaTest {
    private val activityBasedPlayerFormula = ActivityBasedPlayerFormula()
    private val alwaysFirstFormula = AlwaysFirstFormula()

    @Test
    fun givesTheSameAsActivityBasedOrHigherThanPlayer() {
        for (round in IntRange(0, 100_000)) {
            val totalCurrentUserScore = Random.nextInt(0, 500)
            val previousPeriodUserScore = Random.nextInt(0, 500)
            val randomizationSeed = Random.nextLong()
            val periodLength = Random.nextInt(7, 120)
            val activeDaysMin = Random.nextInt(1, periodLength - 1 )
            val activeDaysMax = Random.nextInt(activeDaysMin + 1, periodLength)
            val minScorePercentage = Random.nextDouble(0.1, 0.9)
            val maxScorePercentage = Random.nextDouble(minScorePercentage, 1.0)
            val scoreVariation = Range (minScorePercentage, maxScorePercentage)


            for (day in 1..periodLength) {
                val coreParams = CoreParams( totalCurrentUserScore,
                    previousPeriodUserScore,
                    day,
                    periodLength,
                    randomizationSeed)
                val resultActivityBased = activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin,
                        activeDaysMax,
                        scoreVariation,
                        coreParams
                    )
                )
                val resultAlwaysFirst = alwaysFirstFormula.calculate(
                    FormulaParameters(
                        activeDaysMin,
                        activeDaysMax,
                        scoreVariation,
                        coreParams
                    )
                )

                if (totalCurrentUserScore * 1.10 < resultActivityBased) {
                    assertEquals(resultActivityBased, resultAlwaysFirst, "In case of low user score - it should give the same result as activity based(day=$day, randomizationSeed=$randomizationSeed, totalCurrentUserScore=$totalCurrentUserScore, periodLength=$periodLength, resultAlwaysFirst=$resultAlwaysFirst,  previousPeriodUserScore=$previousPeriodUserScore)")
                } else {
                    assertTrue("Score from always first should always grant the first place(day=$day, randomizationSeed=$randomizationSeed, totalCurrentUserScore=$totalCurrentUserScore, periodLength=$periodLength, resultAlwaysFirst=$resultAlwaysFirst,  previousPeriodUserScore=$previousPeriodUserScore)") { totalCurrentUserScore < resultAlwaysFirst }
                    val resultDelta = resultAlwaysFirst - totalCurrentUserScore
                    assertTrue("Score from always first should not be significantly bigger(day=$day, randomizationSeed=$randomizationSeed, totalCurrentUserScore=$totalCurrentUserScore, periodLength=$periodLength,  resultAlwaysFirst=$resultAlwaysFirst,  previousPeriodUserScore=$previousPeriodUserScore)") {
                        resultAlwaysFirst < totalCurrentUserScore.toDouble() * 1.11 || (resultDelta in 1..10)  }
                }
            }
        }
    }
}