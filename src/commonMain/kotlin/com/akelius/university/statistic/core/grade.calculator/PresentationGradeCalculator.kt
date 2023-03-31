package com.akelius.university.statistic.core.grade.calculator

class PresentationGradeCalculator {

    /**
     * We always give 5 for:
     * - presentations
     * - when weight is 0, and we do branching for storytelling
     * - when score is not the goal, and weights are 0
     */
    fun scoreToFifths(score: Double) = 5
}