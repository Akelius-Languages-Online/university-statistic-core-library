package com.akelius.university.statistic.core.monthly.race

import com.akelius.university.statistic.core.dto.RaceParameters
import com.akelius.university.statistic.core.dto.RaceResult
import com.akelius.university.statistic.core.dto.RaceResultEntry
import com.akelius.university.statistic.core.monthly.race.NamesGenerator.Companion.userNameStub
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.CoreParams
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.FormulaParameters
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.Range
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.options.ActivityBasedPlayerFormula
import com.akelius.university.statistic.core.monthly.race.simulated.formulas.options.AlwaysFirstFormula
import kotlin.random.Random

class RaceCalculator(private val namesGenerator: NamesGenerator = NamesGenerator()) {

    private val activityBasedPlayerFormula = ActivityBasedPlayerFormula()
    private val alwaysFirstFormula = AlwaysFirstFormula()

    fun race(params: RaceParameters): RaceResult {
        val names = namesGenerator.generate(
            6,
            params.randomizationSeed
        )
        val random = Random(
            params.randomizationSeed.currentYear
                    * params.randomizationSeed.currentMonth
                    * (params.randomizationSeed.userSpecificSeed ?: 1)
        )

        val randomizationSeed = random.nextLong()

        val coreParams = CoreParams(
            lengthOfPeriod = params.lengthOfPeriod,
            currentDayInPeriod = params.currentDayInPeriod,
            previousPeriodUserScore = params.previousPeriodUserScore,
            totalCurrentUserScore = params.totalCurrentUserScore,
            randomizationSeed = randomizationSeed
        )
        val playerResults = listOf(
            RaceResultEntry(
                userNameStub, params.totalCurrentUserScore
            ),
            // Playing strategy: sleepy
            RaceResultEntry(
                names[0], activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = 3,
                        activeDaysMax = 6,
                        percentageRangeOfMaxScore = Range(0.15, 0.25),
                        core = coreParams
                    )
                )
            ),
            // Playing strategy: sleepy
            RaceResultEntry(
                names[1], activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = 4,
                        activeDaysMax = 7,
                        percentageRangeOfMaxScore = Range(0.20, 0.35),
                        core = coreParams
                    )
                )
            ),
            // Playing strategy: busy 1
            RaceResultEntry(
                names[2], activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = 4,
                        activeDaysMax = 6,
                        percentageRangeOfMaxScore = Range(0.30, 0.45),
                        core = coreParams
                    )
                )
            ),
            // Playing strategy: busy 2
            RaceResultEntry(
                names[3], activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = 4,
                        activeDaysMax = 8,
                        percentageRangeOfMaxScore = Range(0.40, 0.65),
                        core = coreParams
                    )
                )
            ),
            // Playing strategy: regular 1
            RaceResultEntry(
                names[4], activityBasedPlayerFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = coreParams.lengthOfPeriod / 2,
                        activeDaysMax = coreParams.lengthOfPeriod,
                        percentageRangeOfMaxScore = Range(0.60, 0.90),
                        core = coreParams
                    )
                )
            ),
            // Playing strategy: hardcore first
            RaceResultEntry(
                names[5], alwaysFirstFormula.calculate(
                    FormulaParameters(
                        activeDaysMin = 20,
                        activeDaysMax = params.lengthOfPeriod,
                        percentageRangeOfMaxScore = Range(0.85, 0.95),
                        core = coreParams
                    )
                )
            )
        ).sortedByDescending { it.score }


        return RaceResult(playerResults, playerResults.indexOfFirst { it.name ==  userNameStub})
    }
}