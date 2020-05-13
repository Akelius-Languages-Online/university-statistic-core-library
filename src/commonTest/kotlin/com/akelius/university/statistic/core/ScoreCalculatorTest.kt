package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScore
import kotlin.test.Asserter
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreCalculatorTest {
    @Test
    fun success() {

        val slideshowScore = SlideshowScore(listOf(SlideScore(correct = true, score = 1.0)))

        val result = ScoreCalculator().calculate(slideshowScore)
        assertEquals(5, result.score)
    }
}