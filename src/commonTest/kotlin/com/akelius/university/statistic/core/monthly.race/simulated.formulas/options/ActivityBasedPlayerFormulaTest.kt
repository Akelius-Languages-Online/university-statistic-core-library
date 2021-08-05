package com.akelius.university.statistic.core.monthly.race.simulated.formulas.options

import com.akelius.university.statistic.core.monthly.race.simulated.formulas.CoreParams
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.FormulaParameters
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.Range
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.SimulatedPlayerFormula.Companion.maxScoreCalculatedOnUsers
import kotlin.random.Random
import kotlin.test.*

class ActivityBasedPlayerFormulaTest {
    private val sleepyUserFormula = ActivityBasedPlayerFormula()

    @Test
    fun successfullyReachesMaxWithUserScore() {
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
            var previousResult = 0
            var result = 0
            var resultGrownInValueTimes = 0;

            for (day in 1..periodLength) {
                val coreParams = CoreParams( totalCurrentUserScore,
                    previousPeriodUserScore,
                    day,
                    periodLength,
                    randomizationSeed)

                result = sleepyUserFormula.calculate(
                    FormulaParameters(
                        activeDaysMin,
                        activeDaysMax,
                        scoreVariation,
                        coreParams
                    )
                )
                if (result > previousResult) {
                    resultGrownInValueTimes++
                }
                assertFalse("Value decreases through days(randomizationSeed=$randomizationSeed, periodLength=$periodLength, day=$day, result=$result, previousResult = $previousResult, previousPeriodUserScore=$previousPeriodUserScore)") {
                    previousResult > result
                }
                previousResult = result
            }
            assertTrue ("Value grown $resultGrownInValueTimes times when expected [$activeDaysMin, $activeDaysMax](randomizationSeed=$randomizationSeed, periodLength=$periodLength, result=$result, previousResult = $previousResult, previousPeriodUserScore=$previousPeriodUserScore)") {
                activeDaysMax >= resultGrownInValueTimes || activeDaysMin <= resultGrownInValueTimes }

            val minScore = maxScoreCalculatedOnUsers * scoreVariation.min
            val maxScore = maxScoreCalculatedOnUsers * scoreVariation.max
            assertTrue("Result=$result is not in range [$minScore;$maxScore] (randomizationSeed=$randomizationSeed, periodLength=$periodLength, result=$result, previousResult = $previousResult, previousPeriodUserScore=$previousPeriodUserScore)") {
                result <= maxScore || result >= minScore
            }
        }
    }

    @Test
    fun resultIsConsistentOnTheSameParameters() {
        val totalCurrentUserScore = 10
        val randomizationSeed = 10000L
        val periodLength = 90
        val activeDaysMin = 36
        val activeDaysMax = 48
        val day = 87
        val previousPeriodUserScore = 0
        val coreParams = CoreParams( totalCurrentUserScore,
            previousPeriodUserScore,
            day,
            periodLength,
            randomizationSeed)

        var firstResult = sleepyUserFormula.calculate(
            FormulaParameters(
                activeDaysMin,
                activeDaysMax,
                Range(0.24, 0.30),
                coreParams
            )
        )
        for (round in IntRange(0, 1_000)) {

            assertEquals(
                firstResult, sleepyUserFormula.calculate(
                    FormulaParameters(
                        activeDaysMin,
                        activeDaysMax,
                        Range(0.24, 0.30),
                        coreParams
                    )
                ), "Result is not consistent but simply random"
            )
        }
    }
}