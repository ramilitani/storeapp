package com.softwareangels.storeapp.dto

import java.sql.Timestamp

class ErrorDto(
    val timestamp: Timestamp,
    val status: Int,
    val message: String
)