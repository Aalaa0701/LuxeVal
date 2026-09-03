package com.example.luxeval.domain

import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.Uuid

object DocumentIdGenerator {
    fun next(): String {
        val year = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
        val uniquePart = Uuid.random()
            .toString()
            .replace("-", "")
            .take(8)
            .uppercase()

        return "LV-$year-$uniquePart"
    }
}