package minesweeper.controller.model

data class BoardResponse(val sizeHorizontal: Int,
                         val sizeVertical: Int,
                         val mines: Int,
                         val board: List<List<CellResponse>>
)