package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.CalculationType
import com.akelius.university.statistic.core.dto.SlideScore
import com.akelius.university.statistic.core.dto.SlideshowScoreResult
import com.akelius.university.statistic.core.dto.SlideshowScore
import com.akelius.university.statistic.core.grade.calculator.GradeCalculator
import kotlin.math.abs

/**
 * Score calculation rules as per SCHOOLPF-4086:
 *
 * 1) Presentation where all slides that have score:0, weight:0 should count automatically as full(5) points.
 *
 * 2) Then we have mistake based formula, for every mistake made, one point is deduced from maximum(5)
 *
 *      This applies to slideshows with:
 *          - 1 interaction
 *          - 5 interactions
 *          - 10 interactions
 *
 *  3) Percentage formula that applies to slideshows with 20+ interactions
 *
 *      Here we have two different percentages of correct answers required for passing:
 *
 *          - 80% (normal case) is required for 20+ interactions and quiz unlimited
 *          - 60% is required for QUIZES
 *
 *      Percentage formula will be picked based on CalculationType enum sent from client
 *
 *  Interactions will be recognized if they have weight>0 and score>0.
 *
 */
class ScoreCalculator {

    companion object {
        const val calculationPrecision = 0.00000001
        val gradeCalculator = GradeCalculator()
    }

    fun calculate(score: SlideshowScore): SlideshowScoreResult {
        val calculation = Calculation()

        if (score.slideScores.isNotEmpty() && score.slideScores.find { it.weight > 0 } == null) {
            // if there is not a single weight bigger than zero, this is presentation where we give always 5 points
            return SlideshowScoreResult(
                score = 5,
                scaledScore = 1.0,
                correctAnswersCount = score.slideScores.size,
                totalAnswersCount = score.slideScores.size
            )
        }

        score.slideScores.asSequence().forEach {
            calculation.addSlideScore(it)
        }
        val scaledScore = calculateScaledScore(calculation.userScore, calculation.maxScore)

        when (score.slideScores.count { it.weight > 0 }) {
            1 -> {
                val scoreInFifths = gradeCalculator.scoreToFifths(score.type, scaledScore)
                val interaction = score.slideScores.find { it.weight > 0 }
                return SlideshowScoreResult(
                    score = scoreInFifths,
                    scaledScore = scaledScore,
                    correctAnswersCount = if(interaction!!.isCorrect) 1 else 0,
                    totalAnswersCount = 1
                )
            }
            in 2..19 -> {
                val mistakes = calculateMistakes(score)

                val scoreInFifths = gradeCalculator.scoreToFifths(CalculationType.MISTAKE, mistakes.toDouble())
                return SlideshowScoreResult(
                    score = scoreInFifths,
                    scaledScore = scaledScore,
                    correctAnswersCount = calculation.correctAnswersCount,
                    totalAnswersCount = calculation.totalAnswers
                )
            }
            else -> {
                val scoreInFifths = gradeCalculator.scoreToFifths(score.type, scaledScore)
                return SlideshowScoreResult(
                    score = scoreInFifths,
                    scaledScore = scaledScore,
                    correctAnswersCount = calculation.correctAnswersCount,
                    totalAnswersCount = calculation.totalAnswers
                )
            }
        }

    }

    private fun calculateMistakes(score: SlideshowScore): Int {
        return score.slideScores.count { !it.isCorrect }

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