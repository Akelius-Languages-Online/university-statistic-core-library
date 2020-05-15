package com.akelius.university.statistic.core

class FifthGradeCalculator {

    companion object {
        fun scoreToFifths(score: Double): Int {
            var percentage: Int = (score * 100).toInt()
            return when (percentage) {
                in 80 until 85 -> 1
                in 85 until 90 -> 2
                in 90 until 95 -> 3
                in 95 until 100 -> 4
                100 -> 5
                else -> 0
            }
        }
    }
}