package com.akelius.university.statistic.core.grade.calculator

class QuizFifthGradeCalculator {

    fun scoreToFifths(score: Double): Int {
        var percentage: Int = (score * 100).toInt()
        return when (percentage) {
            in 60 until 70 -> 1
            in 70 until 80 -> 2
            in 80 until 90 -> 3
            in 90 until 100 -> 4
            100 -> 5
            else -> 0
        }
    }
}