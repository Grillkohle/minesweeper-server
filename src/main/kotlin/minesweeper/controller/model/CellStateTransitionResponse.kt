package minesweeper.controller.model

data class CellStateTransitionResponse(
        val gameState: GameResponseState = GameResponseState.IN_PROGRESS,
        val changedCells: List<CellResponse>
)