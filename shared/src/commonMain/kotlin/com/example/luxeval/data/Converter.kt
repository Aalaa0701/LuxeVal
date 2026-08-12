package com.example.luxeval.data

import androidx.room.TypeConverter

class ListStringConverters {

    @TypeConverter
    fun fromList(list: List<String>?): String =
        list?.joinToString(DELIMITER) ?: ""

    @TypeConverter
    fun toList(data: String?): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(DELIMITER)

    private companion object {
        // U+2016 DOUBLE VERTICAL LINE — safe against commas/pipes in file paths or URLs
        const val DELIMITER = "‖"
    }
}