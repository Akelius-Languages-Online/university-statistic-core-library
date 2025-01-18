package com.akelius.university.statistic.core.dto

enum class CalculationType {
    DEFAULT,
    QUIZ,     // the score is easier on students during QUIZ
    QUIZ_UNLIMITED,     // the score is easier on students during QUIZ
    QUIZ_MULTIPLE,
    MISTAKE,
    PRESENTATION
}
