package com.akelius.university.statistic.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FifthGradeCalculatorTest {
    private val scoreEpsilon = 0.001

    @Test
    fun successWithNotZero() {
        var score = 0.80
        while (score <= 1.0) {
            val fifth = FifthGradeCalculator.scoreToFifths(score)
            assertTrue ( fifth > 0, "Score $score gave fifth $fifth")
            assertTrue ( fifth <= 5, "Score $score gave fifth $fifth")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesLowerBound () {
        var score = -1.0
        while (score < 0.80) {
            assertEquals(0, FifthGradeCalculator.scoreToFifths(score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesUpperBound () {
        var score = 1.1 + scoreEpsilon
        while (score < 2.0) {
            assertEquals(0, FifthGradeCalculator.scoreToFifths(score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }
}