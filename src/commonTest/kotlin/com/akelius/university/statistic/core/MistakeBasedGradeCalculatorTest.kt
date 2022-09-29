package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.grade.calculator.GradeCalculator
import kotlin.test.Test
import kotlin.test.assertEquals

class MistakeBasedGradeCalculatorTest {
    private val gradeCalculator = GradeCalculator()

    @Test
    fun testMistakeBasedCalculator() {
        // number of mistakes -> score in fifths
        var mistakesToFifth = mapOf(
            0 to 5,
            1 to 4,
            2 to 3,
            3 to 2,
            4 to 1,
            5 to 0,
            6 to 0
        )

        mistakesToFifth.keys.forEach {mistakes ->
            val fifth = gradeCalculator.scoreToFifths(CalculationType.MISTAKE, mistakes.toDouble())
            assertEquals(mistakesToFifth[mistakes], fifth, "$mistakes of mistakes gave fifth $fifth")
        }

    }
}