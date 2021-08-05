package com.akelius.university.statistic.core.dto

data class RaceResult(val race: List<RaceResultEntry>, val playerPositionIndex: Int)

data class RaceResultEntry(val name: String, val score: Int)