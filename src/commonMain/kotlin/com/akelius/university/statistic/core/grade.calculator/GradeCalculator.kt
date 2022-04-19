package com.akelius.university.statistic.core.grade.calculator

import com.akelius.university.statistic.core.dto.CalculationType

class GradeCalculator {
    private val defaultCalculator = DefaultFifthGradeCalculator()
    private val quizCalculator = QuizFifthGradeCalculator()

    fun scoreToFifths(calculationType: CalculationType, score: Double): Int {
        return if(calculationType == CalculationType.QUIZ) {
            quizCalculator.scoreToFifths(score)
        } else {
            defaultCalculator.scoreToFifths(score)
        }

    }
}