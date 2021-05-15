package minesweeper.controller.model

data class CellStateTransitionResponse(
        val gameState: GameStateResponse,
        val changedCells: List<CellResponse>
)