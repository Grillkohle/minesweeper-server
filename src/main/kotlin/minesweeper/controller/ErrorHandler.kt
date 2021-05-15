package minesweeper.controller

import minesweeper.controller.model.Problem
import minesweeper.service.exception.ResourceNotFoundException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler {
    private val log = KotlinLogging.logger { }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
                .body(Problem(
                        title = "Bad Request",
                        detail = "Missing parameter: ${exception.parameterName}",
                        status = HttpStatus.BAD_REQUEST.value()))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
                .body(Problem(
                        title = "Bad Request",
                        detail = "Missing request body: ${exception.message}",
                        status = HttpStatus.BAD_REQUEST.value()))
    }
    
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ResponseEntity<Problem> {
        log.error("Unknown exception: ", exception)
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(Problem(
                        title = "Not Found",
                        detail = exception.message ?: "Failed to find resource.",
                        status = HttpStatus.NOT_FOUND.value()
                ))
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