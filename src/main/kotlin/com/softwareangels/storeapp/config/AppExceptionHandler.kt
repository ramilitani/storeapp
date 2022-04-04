package com.softwareangels.storeapp.config

import com.softwareangels.storeapp.dto.ErrorDto
import com.softwareangels.storeapp.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class AppExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = arrayOf(ValidationException::class))
    fun validationException(ex: ValidationException) = buildResponseEntity(ex.error)

    fun buildResponseEntity(error: ErrorDto): ResponseEntity<Any> {
        return ResponseEntity(error, HttpStatus.valueOf(error.status))
    }
}