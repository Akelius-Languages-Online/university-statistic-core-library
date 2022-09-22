package com.akelius.university.statistic.core.grade.calculator

class MistakeBasedGradeCalculator {

    fun scoreToFifths(mistakes: Double)= when (mistakes.toInt()) {
            0 -> 5
            1 -> 4
            2 -> 3
            3 -> 2
            4 -> 1
            else -> 0
    }
}