package minesweeper.controller.model

data class CellResponse(val horizontalIndex: Int,
                        val verticalIndex: Int,
                        val numberOfAdjacentMines: Int? = null,
                        val state: CellResponseState = CellResponseState.CONCEALED,
                        val isMine: Boolean? = false
)

enum class CellResponseState {
    CONCEALED,
    REVEALED,
    FLAGGED
}