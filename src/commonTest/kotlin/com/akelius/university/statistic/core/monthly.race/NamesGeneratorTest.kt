package com.akelius.university.statistic.core.monthly.race

import com.akelius.university.statistic.core.dto.RandomizationSeed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NamesGeneratorTest {

    private val namesGenerator = NamesGenerator()

    @Test
    fun `Generates the same name after multiple invocations`() {
        val seed = RandomizationSeed(2022, 1)
        val size = 6
        val firstGeneration = namesGenerator.generate(6, seed)
        assertEquals(size, firstGeneration.size)
        for (namePosition in 0 until size) {
            assertTrue { firstGeneration[namePosition].length > 3 }
        }

        for (index in 1..100) {
            val nextGeneration = namesGenerator.generate(6, seed)
            assertEquals(size, nextGeneration.size)
            for (namePosition in 0 until size) {
                assertEquals(firstGeneration[namePosition], nextGeneration[namePosition])
            }
        }
    }

    @Test
    fun `Generates enough unique names`() {
        val size = 6
        val uniqueGeneratedNames = mutableSetOf<String>()
        for (year in 2020..2050) {
            for (month in 1..12) {
                val seed = RandomizationSeed(year, month)
                val generation = namesGenerator.generate(size, seed)
                uniqueGeneratedNames.addAll(generation)

                assertEquals(size, generation.toSet().size, "Duplicate names are not allowed")
            }
        }

        assertTrue(
            uniqueGeneratedNames.size > 200,
            "It is expected that out of 300 unique names most of them would " +
                    "be covered in 360 generations (12 month * 30 years)"
        )
    }
}