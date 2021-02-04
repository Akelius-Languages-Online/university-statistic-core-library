package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.dto.SlideshowScore
import com.akelius.university.statistic.core.grade.calculator.DefaultFifthGradeCalculator
import com.akelius.university.statistic.core.grade.calculator.GradeCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultFifthGradeCalculatorTest {
    private val scoreEpsilon = 0.001
    private val gradeCalculator = GradeCalculator()

    @Test
    fun successWithNotZero() {
        var score = 0.80
        while (score <= 1.0) {
            val fifth = gradeCalculator.scoreToFifths(CalculationType.DEFAULT, score)
            assertTrue ( fifth > 0, "Score $score gave fifth $fifth")
            assertTrue ( fifth <= 5, "Score $score gave fifth $fifth")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesLowerBound () {
        var score = -1.0
        while (score < 0.80) {
            assertEquals(0, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesUpperBound () {
        var score = 1.1 + scoreEpsilon
        while (score < 2.0) {
            assertEquals(0, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }

    @Test
    fun testExactBoundaries() {
        assertEquals(1, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.8))
        assertEquals(1, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.84999))
        assertEquals(2, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.85))
        assertEquals(2, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.8999))
        assertEquals(3, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.90))
        assertEquals(3, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.94999))
        assertEquals(4, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.95))
        assertEquals(4, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 0.9999))
        assertEquals(5, gradeCalculator.scoreToFifths(CalculationType.DEFAULT, 1.0))
    }
}