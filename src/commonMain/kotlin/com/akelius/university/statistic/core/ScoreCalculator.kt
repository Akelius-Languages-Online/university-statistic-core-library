package com.akelius.university.statistic.core

import com.akelius.university.statistic.core.dto.SlideshowScoreResult
import com.akelius.university.statistic.core.dto.SlideshowScore

class ScoreCalculator {

    // TODO: split calculation of separated slides as this data needs to be stored in statements in BE
    // This would mean that different interface with different input/result types might be as well exposed
    fun calculate(score: SlideshowScore): SlideshowScoreResult {
        var binaryWeightedScore = 0.0
        var scaledWeightedScore = 0.0

        var maxBinaryScore = 0.0
        var maxScaledScore = 0.0

        score.answers.asSequence()
            .forEach {
                if (it.score == null) {
                    val binaryScore = if (it.correct) {
                        1
                    } else {
                        0
                    }
                    binaryWeightedScore += binaryScore * it.weight
                    maxBinaryScore += 1.0 * it.weight
                } else {
                    scaledWeightedScore += it.score * it.weight
                    maxScaledScore += it.maxScore * it.weight
                    //TODO: consider weight 0.0 and divide final score
                }

            }

        val userScore = binaryWeightedScore + scaledWeightedScore
        val maxScore = maxBinaryScore + maxScaledScore

        val scaledScore = userScore / maxScore
        val scoreInFifths = FifthGradeCalculator.scoreToFifths(scaledScore)

        return SlideshowScoreResult(
            scoreInFifths,
            scaledScore,
            0, //TODO: calculate as this number are used in FE
            10
        )
    }
}