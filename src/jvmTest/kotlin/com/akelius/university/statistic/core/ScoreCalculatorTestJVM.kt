package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScore
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreCalculatorTestJVM {
    @Test
    fun success() {
        val slideshowScore = SlideshowScore(listOf(SlideScore(isCorrect = true, score = 1.0)), false)

        val result = ScoreCalculator().calculate(slideshowScore)
        assertEquals(5, result.score)
    }

    @Test
    fun successWithArgumentForMultipleTests() {
        val slideshowScore = SlideshowScore(listOf(SlideScore(isCorrect = true, score = 1.0)), false, "TEST_GRAMMAR")

        val result = ScoreCalculator().calculate(slideshowScore)
        assertEquals(5, result.score)
    }
}