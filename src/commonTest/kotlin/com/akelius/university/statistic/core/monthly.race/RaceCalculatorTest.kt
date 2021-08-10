package com.akelius.university.statistic.core.monthly.race

import com.akelius.university.statistic.core.dto.RaceParameters
import com.akelius.university.statistic.core.dto.RandomizationSeed
import kotlin.test.*

class RaceCalculatorTest {

    private val raceCalculator = RaceCalculator()

    @Test
    fun `At least once one player overruns other during the period`() {
        for (year in IntRange(2020, 2100)) {
            for (previousScore in arrayOf(0, 100, 200, 300)) {
                for (userScore in IntRange(0, 300)) {
                    var positionsChangedTimes = 0;
                    var previousOrderedPositions: List<String>? = null
                    for (day in IntRange(1, 30)) {
                        val params = RaceParameters(
                            totalCurrentUserScore = userScore,
                            previousPeriodUserScore = previousScore,
                            currentDayInPeriod = day,
                            lengthOfPeriod = 30,
                            randomizationSeed = RandomizationSeed(year, year % 12 + 1)
                        )
                        val race = raceCalculator.race(params)
                        val orderedPositions = race.race.map { it.name }
                        if (previousOrderedPositions != null && !previousOrderedPositions.equals(orderedPositions)) {
                            positionsChangedTimes++
                        }
                        previousOrderedPositions = orderedPositions
                    }
                    if (positionsChangedTimes < 1) {
                        fail("It is a stale movement, not a race!")
                    }
                }
            }
        }
    }

    @Test
    fun `User can have 7 to 2 place depending on results at any given moment in the race`() {
        for (year in IntRange(2020, 3000)) {
            for (previousScore in arrayOf(0, 100, 200, 300)) {
                val positionToReachedTimes = mutableMapOf<Int, Int>(
                    1 to 0,
                    2 to 0,
                    3 to 0,
                    4 to 0,
                    5 to 0,
                    6 to 0,
                    7 to 0
                )
                for (userScore in IntRange(0, 300)) {
                    val params = RaceParameters(
                        totalCurrentUserScore = userScore,
                        previousPeriodUserScore = previousScore,
                        currentDayInPeriod = 30,
                        lengthOfPeriod = 30,
                        randomizationSeed = RandomizationSeed(year, year % 12 + 1)
                    )
                    val race = raceCalculator.race(params)
                    if (race.playerPositionIndex == 0) {
                        fail("Player can not reach top 1 position")
                    }
                    positionToReachedTimes[race.playerPositionIndex + 1] =
                        (positionToReachedTimes[race.playerPositionIndex + 1] ?: 0) + 1
                }
                assertTrue { (positionToReachedTimes[1] ?: 0) == 0 }
                for (position in IntRange(2, 7)) {
                    if ((positionToReachedTimes[position] ?: 0) == 0) {
                        fail("Year=$year, previousScore=$previousScore")
                    }
                }
            }
        }
    }

    @Test
    fun `User is always placed on 7 position in case of 0 points`() {
        for (year in IntRange(2020, 3000)) {
            for (month in IntRange(1, 12)) {
                val params = RaceParameters(
                    totalCurrentUserScore = 0,
                    previousPeriodUserScore = 0,
                    currentDayInPeriod = 30,
                    lengthOfPeriod = 30,
                    randomizationSeed = RandomizationSeed(year, month)
                )
                val race = raceCalculator.race(params)
                assertEquals(NamesGenerator.userNameStub, race.race[race.playerPositionIndex].name, params.toString())
                assertEquals(0, race.race[race.playerPositionIndex].score, params.toString())
            }
        }
    }

}