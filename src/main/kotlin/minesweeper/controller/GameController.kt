package minesweeper.controller

import minesweeper.controller.model.Problem
import minesweeper.service.GameService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class GameController(
        private val gameService: GameService
) {
    @PostMapping("/games")
    fun createGame(
            @RequestParam("size_horizontal") sizeHorizontal: Int,
            @RequestParam("size_vertical") sizeVertical: Int,
            @RequestParam("mines") mines: Int,
            builder: UriComponentsBuilder
    ): ResponseEntity<*> {
        validateInput(sizeHorizontal, sizeVertical, mines)?.let { return it }

        val gameResponse = gameService.createGame(
                sizeHorizontal,
                sizeVertical,
                mines)

        return ResponseEntity
                .created(builder.path("games/${gameResponse.id}")
                        .build()
                        .toUri())
                .body(gameResponse)
    }

    private fun validateInput(sizeHorizontal: Int,
                              sizeVertical: Int,
                              mines: Int): ResponseEntity<Problem>? {
        val errorMessages = mutableListOf<String>()

        if (sizeHorizontal <= 0)
            errorMessages += "size_horizontal must be greater than 0"

        if (sizeVertical <= 0)
            errorMessages += "size_horizontal must be greater than 0"

        if (mines <= 0)
            errorMessages += "mines must be greater than 0"

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