package minesweeper.controller.model

data class CellStateTransitionResponse(
        val gameState: GameResponseState,
        val changedCells: List<CellResponse>
)