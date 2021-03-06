package minesweeper.repository.entity

data class BoardEntity(val horizontalSize: Int,
                       val verticalSize: Int,
                       val cells: List<List<CellEntity>>,
                       val numberOfMines: Int) {
    val sizeCoordinates = Pair(horizontalSize - 1, verticalSize - 1)
}