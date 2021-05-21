package minesweeper.controller

import minesweeper.controller.model.Problem
import minesweeper.service.exception.CellNotModifiableException
import minesweeper.service.exception.GameNotModifiableException
import minesweeper.service.exception.ResourceNotFoundException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ErrorHandler {
    private val log = KotlinLogging.logger { }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
            .body(
                Problem(
                    title = "Bad Request",
                    detail = "Missing parameter: ${exception.parameterName}",
                    status = HttpStatus.BAD_REQUEST.value()
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
            .body(
                Problem(
                    title = "Bad Request",
                    detail = "Missing request body: ${exception.message}",
                    status = HttpStatus.BAD_REQUEST.value()
                )
            )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(exception: ConstraintViolationException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
            .body(
                Problem(
                    title = "Bad Request",
                    detail = "Invalid parameter(s): ${exception.message}",
                    status = HttpStatus.BAD_REQUEST.value()
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<Problem> {
        return ResponseEntity.badRequest()
            .body(
                Problem(
                    title = "Bad Request",
                    detail = "Missing/invalid request parameters: ${exception.allErrors}",
                    status = HttpStatus.BAD_REQUEST.value()
                )
            )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(exception: ResourceNotFoundException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
            .body(
                Problem(
                    title = "Not Found",
                    detail = exception.message ?: "Failed to find resource.",
                    status = HttpStatus.NOT_FOUND.value()
                )
            )
    }

    @ExceptionHandler(value = [GameNotModifiableException::class, CellNotModifiableException::class])
    fun handleGameNotModifiableException(exception: RuntimeException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .body(
                Problem(
                    title = "Unprocessable entity",
                    detail = exception.message
                        ?: "Game/Cell is not modifiable: the requested modification is forbidden.",
                    status = HttpStatus.UNPROCESSABLE_ENTITY.value()
                )
            )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<Problem> {
        log.error("Unknown exception: ", exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(
                Problem(
                    title = "Internal Server Error",
                    detail = "An internal server error occurred.",
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
            )
    }
}