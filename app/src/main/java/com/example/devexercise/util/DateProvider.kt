package com.example.devexercise.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.*

class DateProviderImp constructor(private val clock: Clock): DateProvider{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun now(): ZonedDateTime = ZonedDateTime.now(clock)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun of(localDateTime: LocalDateTime, zoneId: ZoneId): ZonedDateTime = ZonedDateTime.of(localDateTime, zoneId)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun currentDate(): LocalDate = LocalDate.now(clock)
}

interface DateProvider {
    fun now(): ZonedDateTime
    fun of(localDateTime: LocalDateTime, zoneId: ZoneId): ZonedDateTime
    fun currentDate(): LocalDate
}