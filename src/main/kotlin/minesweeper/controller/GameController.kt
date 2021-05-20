package minesweeper.controller

import minesweeper.controller.model.CellStateTransitionRequest
import minesweeper.controller.model.CellStateTransitionResponse
import minesweeper.controller.model.GameResponse
import minesweeper.service.GameService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@Validated
class GameController(
    private val gameService: GameService
) {
    @PostMapping("/games")
    fun createGame(
        @RequestParam("horizontal_size") @Min(1) horizontalSize: Int,
        @RequestParam("vertical_size") @Min(1) verticalSize: Int,
        builder: UriComponentsBuilder
    ): ResponseEntity<GameResponse> {
        val gameResponse = gameService.createGame(
            horizontalSize,
            verticalSize
        )

        return ResponseEntity
            .created(
                builder.path("/games/${gameResponse.id}")
                    .build()
                    .toUri()
            )
            .body(gameResponse)
    }

    @PatchMapping("/games/{gameId}/cells")
    fun changeCellState(
        @PathVariable gameId: UUID,
        @RequestBody @Valid transitionRequest: CellStateTransitionRequest
    ): ResponseEntity<CellStateTransitionResponse> {
        val transitionResponse = gameService.updateCellState(gameId, transitionRequest)

        return ResponseEntity
            .ok()
            .body(transitionResponse)
    }
}