package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScore
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScoreCalculatorTest {
    private val calculationPrecision = 0.0001
    private val calculator = ScoreCalculator()

    @Test
    fun singleSlideCalculatesScoreSuccessfully() {
        val slideshowScore = SlideshowScore(listOf(SlideScore(isCorrect = true, score = 1.0)), false)

        val result = calculator.calculate(slideshowScore)
        assertEquals(5, result.score)
        assertEquals(1, result.totalAnswersCount)
        assertEquals(1, result.correctAnswersCount)
        assertTrue(abs(1.0 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun singleInteractionCalculatesScoreSuccessfully() {
        val slideshowScore = SlideshowScore(listOf(
            SlideScore(isCorrect = true, score = 1.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0)
        ), false)

        val result = calculator.calculate(slideshowScore)
        assertEquals(5, result.score)
        assertEquals(1, result.totalAnswersCount)
        assertEquals(1, result.correctAnswersCount)
        assertTrue(abs(1.0 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun singleInteractionCalculatesScoreSuccessfullyWhenWrong() {
        val slideshowScore = SlideshowScore(listOf(
            SlideScore(isCorrect = false),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
            SlideScore(isCorrect = true, score = 1.0, weight = 0.0)
        ), false)

        val result = calculator.calculate(slideshowScore)
        assertEquals(0, result.score)
        assertEquals(1, result.totalAnswersCount)
        assertEquals(0, result.correctAnswersCount)
        assertTrue(abs(0.0 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun defaultWeightCalculationSuccess() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = true, score = 1.0),
                SlideScore(isCorrect = true)
            ), false
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(5, result.score)
        assertEquals(2, result.totalAnswersCount)
        assertEquals(2, result.correctAnswersCount)
        assertTrue(abs(1.0 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun mistakeBasedCalculationSuccess() {
        val slideshowScore = SlideshowScore(
            slideScores = listOf(
                SlideScore(isCorrect = false, score = null),
                SlideScore(isCorrect = false, score = null),
                SlideScore(isCorrect = true, score = null),
                SlideScore(isCorrect = true, score = null),
                SlideScore(isCorrect = true, score = null)
            ), false
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(3, result.score)
        assertEquals(5, result.totalAnswersCount)
        assertEquals(3, result.correctAnswersCount)
    }

    @Test
    fun presentationScoreCalculatedCorrectly() {
        val slideshowScore = SlideshowScore(
            slideScores = listOf(
                SlideScore(isCorrect = false, score = 0.0, weight = 0.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 0.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 0.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 0.0)
            ), false
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(5, result.score)
        assertEquals(0, result.totalAnswersCount)
        assertEquals(0, result.correctAnswersCount)
        assertEquals(1.0, result.scaledScore)
    }

    @Test
    fun quizScoreCalculatedCorrectly() {
        val slideshowScore = SlideshowScore(
            slideScores = listOf(
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0)
            ), true
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(1, result.score)
        assertEquals(20, result.totalAnswersCount)
        assertEquals(12, result.correctAnswersCount)
        assertEquals(0.6, result.scaledScore)
    }

    @Test
    fun quizMultipleTestsScoreCalculatedCorrectly() {
        val slideshowScore = SlideshowScore(
            slideScores = listOf(
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),

                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = false, score = 0.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0),
                SlideScore(isCorrect = true, score = 1.0, weight = 1.0)
            ), true, "TEST_GRAMMAR"
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(5, result.score)
        assertEquals(20, result.totalAnswersCount)
        assertEquals(12, result.correctAnswersCount)
        assertEquals(0.6, result.scaledScore)
    }

    @Test
    fun defaultWeightsCalculationFailure() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),

                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),

                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),

                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, score = 0.79),
                SlideScore(isCorrect = false)
            ), false
        )

        val result = calculator.calculate(slideshowScore)
        assertEquals(0, result.score)
        assertEquals(20, result.totalAnswersCount)
        assertEquals(0, result.correctAnswersCount)

        val expectedScore = 0.79 / 2.0

        assertTrue(abs(result.scaledScore - expectedScore) < calculationPrecision)
    }

    @Test
    fun doesNotFailOnNoSlides() {
        val result = calculator.calculate(SlideshowScore(emptyList(), false))

        assertEquals(5, result.score)
        assertEquals(0, result.totalAnswersCount)
        assertEquals(0, result.correctAnswersCount)
        assertEquals(1.0, result.scaledScore)
    }

    @Test
    fun weightsWithBinaryScore() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 2.0),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = true, weight = 0.0),

                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 2.0),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = true, weight = 0.0),


                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 2.0),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = true, weight = 0.0),

                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 2.0),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = true, weight = 0.0),

                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 2.0),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = true, weight = 0.0)
            ), false
        )

        val result = calculator.calculate(slideshowScore)

        assertEquals(1, result.score)
        assertEquals(20, result.totalAnswersCount)
        assertEquals(15, result.correctAnswersCount)
        assertTrue(abs(0.836 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun weightsWithScaledScore() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = true, weight = 50.0, score = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 50.0, score = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 50.0, score = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 50.0, score = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 50.0, score = 1.0, maxScore = 1.0),

                SlideScore(isCorrect = false, weight = 0.01, score = 5.0, maxScore = 100.0),
                SlideScore(isCorrect = false, weight = 0.01, score = 5.0, maxScore = 100.0),
                SlideScore(isCorrect = false, weight = 0.01, score = 5.0, maxScore = 100.0),
                SlideScore(isCorrect = false, weight = 0.01, score = 5.0, maxScore = 100.0),
                SlideScore(isCorrect = false, weight = 0.01, score = 5.0, maxScore = 100.0),

                SlideScore(isCorrect = true, weight = 1.0, score = 0.3, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 1.0, score = 0.3, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 1.0, score = 0.3, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 1.0, score = 0.3, maxScore = 1.0),
                SlideScore(isCorrect = true, weight = 1.0, score = 0.3, maxScore = 1.0),

                SlideScore(isCorrect = true, score = 1.0),
                SlideScore(isCorrect = true, score = 1.0),
                SlideScore(isCorrect = true, score = 1.0),
                SlideScore(isCorrect = true, score = 1.0),
                SlideScore(isCorrect = true, score = 1.0)
            ), false
        )

        val result = calculator.calculate(slideshowScore)

        assertEquals(4, result.score)
        assertEquals(15, result.correctAnswersCount)
        assertEquals(20, result.totalAnswersCount)
        assertTrue(abs(0.96886792452 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun calculationWithNegativeScore() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5),
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01),
                SlideScore(isCorrect = true, weight = 20.5)
            ), false
        )

        val result = calculator.calculate(slideshowScore)

        assertEquals(3, result.score)
        assertEquals(20, result.totalAnswersCount)
        assertEquals(10, result.correctAnswersCount)
        assertTrue(abs(0.90697674 - result.scaledScore) < calculationPrecision)
    }

    @Test
    fun singleNegativeScoreDoesNotBreak() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = false, score = -100.0, maxScore = 100.0, weight = 0.01)
            ), false
        )
        val result = calculator.calculate(slideshowScore)

        assertEquals(0, result.score)
        assertEquals(1, result.totalAnswersCount)
        assertEquals(0, result.correctAnswersCount)
        assertEquals(-1.0, result.scaledScore)
    }

    @Test
    fun mixBinaryAndScaledScores() {
        val slideshowScore = SlideshowScore(
            listOf(
                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 3.0),
                SlideScore(isCorrect = true, weight = 0.1),

                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = true, weight = 0.1),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false),
                SlideScore(isCorrect = false, weight = 0.0),

                SlideScore(isCorrect = false, weight = 0.0),
                SlideScore(isCorrect = true, score = 0.8, weight = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, score = 0.8, weight = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, score = 0.8, weight = 1.0, maxScore = 1.0),
                SlideScore(isCorrect = true, score = 0.8, weight = 1.0, maxScore = 1.0),

                SlideScore(isCorrect = true, score = 100.0, weight = 0.05, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.05, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.01, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.01, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.02, maxScore = 100.0),

                SlideScore(isCorrect = true, score = 100.0, weight = 0.05, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.05, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.01, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 100.0, weight = 0.01, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.02, maxScore = 100.0),

                SlideScore(isCorrect = true, score = 80.0, weight = 0.02, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.0, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.0, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.0, maxScore = 100.0),
                SlideScore(isCorrect = true, score = 80.0, weight = 0.0, maxScore = 100.0)
            ), false
        )

        val result = calculator.calculate(slideshowScore)

        assertEquals(3, result.score)
        assertEquals(23, result.totalAnswersCount)
        assertEquals(21, result.correctAnswersCount)
        assertTrue(abs(0.9116997 - result.scaledScore) < calculationPrecision)
    }
}