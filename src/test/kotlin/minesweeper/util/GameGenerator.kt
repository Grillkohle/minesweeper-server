package minesweeper.util

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import java.util.UUID
import kotlin.random.Random

class GameGenerator {
    companion object {
        fun generateGameEntity(sizeHorizontal: Int = Random.nextInt(1, 10),
                               sizeVertical: Int = Random.nextInt(1, 10),
                               mines: Int = Random.nextInt(1, sizeHorizontal)): GameEntity {
            val boardEntity = BoardEntity(
                    sizeHorizontal = sizeHorizontal,
                    sizeVertical = sizeVertical,
                    mines = mines,
                    board = generateBoardArray(sizeHorizontal, sizeVertical))

            return GameEntity(
                    id = UUID.randomUUID(),
                    board = boardEntity)
        }

        private fun generateBoardArray(sizeHorizontal: Int,
                                       sizeVertical: Int): List<List<CellEntity>> {
            val board = mutableListOf<List<CellEntity>>()

            for (columnIndex in 0..sizeHorizontal) {
                val column = mutableListOf<CellEntity>()

                for (rowIndex in 0..sizeVertical) {
                    column.add(rowIndex, CellEntity(columnIndex, rowIndex))
                }

                board.add(columnIndex, column.toList())
            }
            return board.toList()
        }
    }
}