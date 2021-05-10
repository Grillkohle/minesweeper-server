package minesweeper.controller.model

data class BoardResponse(val horizontalSize: Int,
                         val verticalSize: Int,
                         val cells: List<List<CellResponse>>
)