package minesweeper.controller.model

import java.util.UUID

data class GameResponse(
        val id: UUID,
        val board: BoardResponse
)

enum class GameStateResponse {
    IN_PROGRESS,
    LOSS
}