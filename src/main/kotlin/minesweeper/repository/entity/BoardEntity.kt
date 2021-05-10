package minesweeper.repository.entity

data class BoardEntity(val horizontalSize: Int,
                       val verticalSize: Int,
                       val board: List<List<CellEntity>>)