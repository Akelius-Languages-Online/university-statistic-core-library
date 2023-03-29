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
 * 1) Presentation where all slides that have weight:0 should count automatically as full(5) points.
 *
 * 2) Then we have mistake based formula, for every mistake made, one point is deduced from maximum(5)
 *
 *      This applies to slideshows with:
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
 *  4) In slideshow with one slide score is implemented in Interaction itself.
 *  It defaults because of it to default formula
 *
 */
class ScoreCalculator {

    companion object {
        const val calculationPrecision = 0.00000001
        internal val gradeCalculator = GradeCalculator()
    }

    fun calculate(score: SlideshowScore): SlideshowScoreResult {
        val calculation = Calculation()

        score.slideScores.asSequence().forEach {
            calculation.addSlideScore(it)
        }

        val countInteractions = score.slideScores.count { it.weight > 0 }
        val calculationType = calculationFormula(score, countInteractions)

        val scaledScore = calculateScaledScore(calculation.userScore, calculation.maxScore)

        val scoreInFifths = gradeCalculator.scoreToFifths(
            calculationType, if (calculationType == CalculationType.MISTAKE) {
                calculateMistakes(score).toDouble()
            } else {
                scaledScore
            }
        )
        return SlideshowScoreResult(
            score = scoreInFifths,
            scaledScore = scaledScore,
            correctAnswersCount = calculation.correctAnswersCount,
            totalAnswersCount = calculation.totalAnswers
        )
    }

    private fun calculationFormula(
        score: SlideshowScore,
        countInteractions: Int
    ) = if (score.isQuiz) {
        CalculationType.QUIZ
    } else if (countInteractions in 2..19) {
        CalculationType.MISTAKE
    } else if (countInteractions == 0) {
        CalculationType.PRESENTATION
    } else {
        CalculationType.DEFAULT
    }

    private fun calculateMistakes(score: SlideshowScore): Int {
        return score.slideScores.count { !it.isCorrect }
    }

    private fun calculateScaledScore(userScore: Double, maxScore: Double): Double {
        return if (maxScore < calculationPrecision) {
            // no possibility to earn (presentations) = best outcome
            1.0
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