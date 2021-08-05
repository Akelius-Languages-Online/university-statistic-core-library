package com.akelius.university.statistic.core.monthly.race.simulated.formulas.options

import com.akelius.university.statistic.core.monthly.race.simulated.formulas.FormulaParameters
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.Range
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.SimulatedPlayerFormula
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.SimulatedPlayerFormula.Companion.maxScoreCalculatedOnUsers
import kotlin.math.max
import kotlin.random.Random

open class ActivityBasedPlayerFormula : SimulatedPlayerFormula {
    override fun calculate(params: FormulaParameters): Int {
        val coreParams = params.core

        val random = Random(coreParams.randomizationSeed)

        var maxScore = calculateRelative40Percentile(maxScoreCalculatedOnUsers, params.percentageRangeOfMaxScore, random)

        if (coreParams.previousPeriodUserScore > 20) {
            maxScore = calculateRelative40Percentile(coreParams.previousPeriodUserScore, params.percentageRangeOfMaxScore, random)
        }

        val activeDays = random.nextInt(params.activeDaysMin, params.activeDaysMax)

        val sleepUntil = random.nextInt(0, coreParams.lengthOfPeriod - activeDays)
        val playsTill = max(random.nextInt(sleepUntil, coreParams.lengthOfPeriod), sleepUntil + activeDays)


        val playsOnSpecificDays = IntRange(1, activeDays).map { random.nextInt(sleepUntil, playsTill) }.sorted()

        val notNormalizedPointsEarned = playsOnSpecificDays.map { random.nextInt(1, maxScore) }
        val normalizationFactor = maxScore.toFloat() / notNormalizedPointsEarned.sum().toFloat()

        var normalizedPointsEarned = notNormalizedPointsEarned.map { it * normalizationFactor }

        var sum = 0
        for (index in IntRange(0, playsOnSpecificDays.size - 1)) {
            if (index > coreParams.currentDayInPeriod) {
                break
            }
            sum += normalizedPointsEarned[index].toInt()
        }

        return sum
    }

    private fun calculateRelative40Percentile(maxValue: Int, maxScoreRange: Range, random: Random): Int {
        val maxScoreNormalization = random.nextDouble(maxScoreRange.min, maxScoreRange.max)
        return (maxValue.toFloat() * maxScoreNormalization).toInt()
    }
}