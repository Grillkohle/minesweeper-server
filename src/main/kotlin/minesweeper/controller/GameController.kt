package minesweeper.controller

import minesweeper.controller.model.CellStateTransitionRequest
import minesweeper.controller.model.CellStateTransitionResponse
import minesweeper.controller.model.Problem
import minesweeper.service.GameService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import javax.validation.Valid

@RestController
@Validated
class GameController(
        private val gameService: GameService
) {
    @PostMapping("/games")
    fun createGame(
            @RequestParam("horizontal_size") horizontalSize: Int,
            @RequestParam("vertical_size") verticalSize: Int,
            builder: UriComponentsBuilder
    ): ResponseEntity<*> {
        validateCreateGameInput(horizontalSize, verticalSize)?.let { return it }

        val gameResponse = gameService.createGame(
                horizontalSize,
                verticalSize)

        return ResponseEntity
                .created(builder.path("/games/${gameResponse.id}")
                        .build()
                        .toUri())
                .body(gameResponse)
    }

    @PutMapping("/games/{gameId}/cells/state")
    @Validated
    fun changeCellState(@PathVariable gameId: UUID,
                        @RequestBody @Valid transitionRequest: CellStateTransitionRequest
    ): ResponseEntity<CellStateTransitionResponse> {
        val transitionResponse = gameService.updateCellState(gameId, transitionRequest)

        return ResponseEntity
                .ok()
                .body(transitionResponse)
    }

    private fun validateCreateGameInput(horizontalSize: Int,
                                        verticalSize: Int): ResponseEntity<Problem>? {
        val errorMessages = mutableListOf<String>()

        if (horizontalSize <= 0)
            errorMessages += "horizontal_size must be greater than 0"

        if (verticalSize <= 0)
            errorMessages += "vertical_size must be greater than 0"

        return if (errorMessages.isEmpty()) null else buildProblem(errorMessages)
    }

    private fun buildProblem(errorMessages: List<String>): ResponseEntity<Problem> {
        return ResponseEntity
                .badRequest()
                .body(Problem(title = "Bad request",
                        status = HttpStatus.BAD_REQUEST.value(),
                        detail = errorMessages.joinToString(separator = ",")
                ))
    }
}