package com.erkprog.rateit.model

enum class Rating(val title: String, val value: Int) {
    HIDEOUS("Terrible", 1),
    OK("Bad", 2),
    AVERAGE("Average", 3),
    GOOD("Good", 4),
    EXCELLENT("Excellent" ,5)
}