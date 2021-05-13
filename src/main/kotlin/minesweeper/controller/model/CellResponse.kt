package minesweeper.controller.model

data class CellResponse(val horizontalIndex: Int,
                        val verticalIndex: Int,
                        val isMine: Boolean
)