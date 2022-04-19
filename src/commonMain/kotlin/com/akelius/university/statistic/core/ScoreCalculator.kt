package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScoreResult
import com.akelius.university.statistic.core.dto.SlideshowScore
import com.akelius.university.statistic.core.grade.calculator.DefaultFifthGradeCalculator
import com.akelius.university.statistic.core.grade.calculator.GradeCalculator
import kotlin.math.abs

class ScoreCalculator {

    companion object {
        const val calculationPrecision = 0.00000001
        val gradeCalculator = GradeCalculator()
    }

    fun calculate(score: SlideshowScore): SlideshowScoreResult {
        val calculation = Calculation()
        val scoreOverride = if (score.slideScores.find { it.weight > 0 } == null) {
            1.0
        } else {
            null
        }

        score.slideScores.asSequence().forEach { calculation.addSlideScore(it, scoreOverride) }

        val scaledScore = calculateScaledScore(calculation.userScore, calculation.maxScore)
        val scoreInFifths = gradeCalculator.scoreToFifths(score.type, scaledScore)

        return SlideshowScoreResult(
            score = scoreInFifths,
            scaledScore = scaledScore,
            correctAnswersCount = calculation.correctAnswersCount,
            totalAnswersCount = calculation.totalAnswers
        )
    }

    private fun calculateScaledScore(userScore: Double, maxScore: Double): Double {
        return if (maxScore < calculationPrecision || userScore < calculationPrecision) {
            0.0
        } else {
            userScore / maxScore
        }
    }

    private data class Calculation(
        var userScore: Double = 0.0,
        var maxScore: Double = 0.0,

        var correctAnswersCount: Int = 0,
        var totalAnswers: Int = 0
    ) {

        fun addSlideScore(slideScore: SlideScore, weightOverride: Double?) {
            if (slideScore.score == null) {
                addBinaryScore(slideScore.isCorrect, weightOverride?: slideScore.weight)
            } else {
                addScore(slideScore.isCorrect, slideScore.score, slideScore.maxScore, weightOverride?: slideScore.weight)
            }
        }

        private fun addBinaryScore(correct: Boolean, weight: Double) {
            val binaryScore = if (correct) {
                1.0
            } else {
                0.0
            }
            addScore(correct, binaryScore, 1.0, weight)
        }

        private fun addScore(correct: Boolean, score: Double, maxSlideScore: Double, weight: Double) {
            userScore += score * weight
            maxScore += maxSlideScore * weight

            if (abs(weight) > calculationPrecision) {
                if (correct) {
                    correctAnswersCount++
                }
                totalAnswers++
            }
        }
    }
}