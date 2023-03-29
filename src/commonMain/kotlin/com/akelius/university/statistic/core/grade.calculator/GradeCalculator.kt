package com.akelius.university.statistic.core.grade.calculator

import com.akelius.university.statistic.core.dto.CalculationType

internal class GradeCalculator {
    private val defaultCalculator = DefaultFifthGradeCalculator()
    private val quizCalculator = QuizFifthGradeCalculator()
    private val mistakeCalculator = MistakeBasedGradeCalculator()
    private val presentationCalculator = PresentationGradeCalculator()

    fun scoreToFifths(calculationType: CalculationType, score: Double): Int {
        return when (calculationType) {
            CalculationType.QUIZ -> {
                quizCalculator.scoreToFifths(score)
            }
            CalculationType.MISTAKE -> {
                mistakeCalculator.scoreToFifths(score)
            }
            CalculationType.PRESENTATION -> {
                presentationCalculator.scoreToFifths(score)
            }
            else -> {
                defaultCalculator.scoreToFifths(score)
            }
        }
    }
}