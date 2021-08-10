package com.akelius.university.statistic.core.monthly.race

class NamesGenerator {

    companion object {
        const val userNameStub = "{{{USER_NAME_STUB}}}"
    }

    fun generate (size: Int, currentYear: Int, currentMonth: Int): List<String> {
        return listOf("Mykola", "Armando", "Ivaylo", "Tomislav", "Roberto", "Oleksandr")
    }
}