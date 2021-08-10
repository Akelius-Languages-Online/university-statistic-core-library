package com.akelius.university.statistic.core.monthly.race.simulated.formulas.options

import com.akelius.university.statistic.core.monthly.race.simulated.formulas.FormulaParameters
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

class AlwaysFirstFormula : ActivityBasedPlayerFormula() {
    override fun calculate(params: FormulaParameters): Int {
        val random = Random(params.core.randomizationSeed)

        val activityBasedResult = max(super.calculate(params).toDouble(), 1.0)
        val moreThanUser = params.core.totalCurrentUserScore * random.nextDouble(1.01, 1.10)
        return ceil(
            max(
                activityBasedResult,
                moreThanUser
            )
        ).toInt()
    }
}