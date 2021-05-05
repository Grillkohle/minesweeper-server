package minesweeper.repository.entity

data class BoardEntity(val sizeHorizontal: Int,
                       val sizeVertical: Int,
                       val mines: Int,
                       val board: List<List<CellEntity>>)