package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScoreResult
import com.akelius.university.statistic.core.dto.SlideshowScore
import kotlin.math.abs

class ScoreCalculator {

    companion object {
        const val calculationPrecision = 0.00000001
    }

    fun calculate(score: SlideshowScore): SlideshowScoreResult {
        val calculation = Calculation()

        score.slideScores.asSequence().forEach { calculation.addSlideScore(it) }

        val scaledScore = calculateScaledScore(calculation.userScore, calculation.maxScore)
        val scoreInFifths = FifthGradeCalculator.scoreToFifths(scaledScore)

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

        fun addSlideScore(slideScore: SlideScore) {
            if (slideScore.score == null) {
                addBinaryScore(slideScore.isCorrect, slideScore.weight)
            } else {
                addScore(slideScore.isCorrect, slideScore.score, slideScore.maxScore, slideScore.weight)
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