package minesweeper.controller

import minesweeper.controller.model.Problem
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler {
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
                .body(Problem(
                        title = "Bad Request",
                        detail = "Missing parameter: ${exception.parameterName}",
                        status = HttpStatus.BAD_REQUEST.value()))
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(Problem(
                        title = "Internal Server Error",
                        detail = "An internal server error occurred.",
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                ))
    }
}