package com.akelius.university.statistic.core.grade.calculator

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.dto.SlideshowScore

class GradeCalculator {
    private val defaultCalculator = DefaultFifthGradeCalculator()
    private val quizCalculator = QuizFifthGradeCalculator()

    fun scoreToFifths(slideshowScore: SlideshowScore, score: Double): Int {
        return if(slideshowScore.type == CalculationType.QUIZ) {
            quizCalculator.scoreToFifths(score)
        } else {
            defaultCalculator.scoreToFifths(score)
        }

    }
}