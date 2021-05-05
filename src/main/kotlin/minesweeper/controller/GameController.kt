package minesweeper.controller

import minesweeper.controller.model.GameResponse
import minesweeper.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class GameController(
        val gameService: GameService
) {
    @PostMapping("/games")
    fun createGame(
            @RequestParam("size_horizontal") sizeHorizontal: Int,
            @RequestParam("size_vertical") sizeVertical: Int,
            @RequestParam("mines") mines: Int,
            builder: UriComponentsBuilder
    ): ResponseEntity<GameResponse> {
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
}