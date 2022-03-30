package com.softwareangels.storeapp.exception

import com.softwareangels.storeapp.dto.ErrorDto

class ValidationException(val error: ErrorDto) : RuntimeException(error.message) {
}