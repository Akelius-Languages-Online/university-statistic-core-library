package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScore
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreCalculatorTestJS {
    @Test
    fun success() {
        val slideshowScore = SlideshowScore(listOf(SlideScore(isCorrect = true, score = 1.0)))

        val result = ScoreCalculator().calculate(slideshowScore)
        assertEquals(5, result.score)
    }

    @Test
    fun successWithArgumentForMultipleTests() {
        val slideshowScore = SlideshowScore(listOf(SlideScore(isCorrect = true, score = 1.0), "TEST_GRAMMAR"))

        val result = ScoreCalculator().calculate(slideshowScore)
        assertEquals(100, result.score)
    }
}