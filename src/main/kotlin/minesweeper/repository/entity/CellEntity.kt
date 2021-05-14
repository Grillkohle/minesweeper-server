package minesweeper.repository.entity

class CellEntity(val horizontalIndex: Int,
                 val verticalIndex: Int,
                 val isMine: Boolean = false,
                 val numberOfAdjacentMines: Int = 0,
                 var state: CellEntityState = CellEntityState.CONCEALED) {
    companion object {
        private val neighborCoordinateOffsets = setOf(Pair(-1, -1), Pair(0, -1), Pair(1, -1),
                                                      Pair(-1, 0),               Pair(1, 0),
                                                      Pair(-1, 1),  Pair(0, 1),  Pair(1, 1))

        fun getNeighborCoordinates(coordinates: Pair<Int, Int>, maxCoordinates: Pair<Int, Int>): Set<Pair<Int, Int>> {
            val (x, y) = coordinates
            val (maxX, maxY) = maxCoordinates

            return neighborCoordinateOffsets
                    .map { (xOffset, yOffset) -> x + xOffset to y + yOffset } // generate indices of all possible neighboring fields
                    .filterNot { (x, y) -> x < 0 || x > maxX || y < 0 || y > maxY } // filter those out which are not on the board
                    .toSet()
        }
    }

    enum class CellEntityState{
        CONCEALED
    }
}
