package minesweeper.util

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import minesweeper.repository.entity.GameEntityState
import java.io.File
import java.util.UUID

class GameImporter {
    companion object {
        private const val MINE_VALUE = '*'

        fun createGameFromFile(fileName: String): GameEntity {
            val cells = mutableListOf<MutableList<CellEntity>>()
            val lines = File(fileName).readLines()

            val verticalSize = lines.size

            for (rowIndex in 0 until verticalSize) {
                val currentRow = lines[rowIndex]
                for (columnIndex in currentRow.indices) {
                    val cellValue = currentRow[columnIndex]

                    var column = cells.getOrNull(columnIndex)
                    if (column == null) {
                        column = mutableListOf()
                        cells.add(column)
                    }
                    column.add(CellEntity(horizontalIndex = columnIndex,
                            verticalIndex = rowIndex,
                            numberOfAdjacentMines = if (cellValue == MINE_VALUE) 0 else Character.getNumericValue(cellValue),
                            isMine = cellValue == MINE_VALUE,
                            state = CellEntity.CellEntityState.CONCEALED))
                }
            }

            val boardEntity = BoardEntity(
                    horizontalSize = cells.size,
                    verticalSize = verticalSize,
                    numberOfMines = cells.map { column -> column.filter { cell -> cell.isMine }.map { 1 }.sum() }.sum(),
                    cells = cells
            )

            return GameEntity(
                    id = UUID.randomUUID(),
                    state = GameEntityState.IN_PROGRESS,
                    board = boardEntity)
        }
    }
}