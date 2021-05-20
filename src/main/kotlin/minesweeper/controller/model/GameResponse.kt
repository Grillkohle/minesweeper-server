package minesweeper.controller.model

import java.util.UUID

data class GameResponse(
        val id: UUID,
        val board: BoardResponse,
        val state: GameResponseState = GameResponseState.IN_PROGRESS
)

enum class GameResponseState {
    IN_PROGRESS,
    LOSS
}