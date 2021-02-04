package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.dto.SlideshowScore
import com.akelius.university.statistic.core.grade.calculator.GradeCalculator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuizFifthGradeCalculatorTest {
    private val scoreEpsilon = 0.001
    private val gradeCalculator = GradeCalculator()

    @Test
    fun successWithNotZero() {
        var score = 0.60
        while (score <= 1.0) {
            val fifth = gradeCalculator.scoreToFifths(CalculationType.QUIZ, score)
            assertTrue ( fifth > 0, "Score $score gave fifth $fifth")
            assertTrue ( fifth <= 5, "Score $score gave fifth $fifth")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesLowerBound () {
        var score = -1.0
        while (score < 0.60) {
            assertEquals(0, gradeCalculator.scoreToFifths(CalculationType.QUIZ, score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }

    @Test
    fun doesNotFailOnUnAppropriateValuesUpperBound () {
        var score = 1.01 + scoreEpsilon
        while (score < 2.0) {
            assertEquals(0, gradeCalculator.scoreToFifths(CalculationType.QUIZ, score), "Score $score gave not zero score")
            score += scoreEpsilon
        }
    }

    @Test
    fun testExactBoundaries() {
        assertEquals(1, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.6))
        assertEquals(1, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.699))
        assertEquals(2, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.7))
        assertEquals(2, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.79))
        assertEquals(3, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.8))
        assertEquals(3, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.89))
        assertEquals(4, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.9))
        assertEquals(4, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 0.999))
        assertEquals(5, gradeCalculator.scoreToFifths(CalculationType.QUIZ, 1.0))
    }
}