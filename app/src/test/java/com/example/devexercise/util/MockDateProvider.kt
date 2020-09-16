package com.example.devexercise.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class MockDateProvider : DateProvider {
    override fun now(): ZonedDateTime {
        return ZonedDateTime.now()
    }

    override fun of(localDateTime: LocalDateTime, zoneId: ZoneId): ZonedDateTime {
        return ZonedDateTime.of(localDateTime, zoneId)
    }

    override fun currentDate(): LocalDate {
        return LocalDate.now()
    }
}