package minesweeper.util

import minesweeper.controller.model.BoardResponse
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.GameResponse
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
                    board = generateBoardEntityArray(sizeHorizontal, sizeVertical))

            return GameEntity(board = boardEntity)
        }

        private fun generateBoardEntityArray(sizeHorizontal: Int,
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

        fun generateGameResponse(sizeHorizontal: Int = Random.nextInt(1, 10),
                                 sizeVertical: Int = Random.nextInt(1, 10),
                                 mines: Int = Random.nextInt(1, 10)): GameResponse {
            val boardResponse = BoardResponse(
                    sizeHorizontal = sizeHorizontal,
                    sizeVertical = sizeVertical,
                    mines = mines,
                    board = generateBoardResponseArray(sizeHorizontal, sizeVertical))

            return GameResponse(
                    id = UUID.randomUUID(),
                    board = boardResponse)
        }

        private fun generateBoardResponseArray(sizeHorizontal: Int,
                                               sizeVertical: Int): List<List<CellResponse>> {
            val board = mutableListOf<List<CellResponse>>()

            for (columnIndex in 0..sizeHorizontal) {
                val column = mutableListOf<CellResponse>()

                for (rowIndex in 0..sizeVertical) {
                    column.add(rowIndex, CellResponse(columnIndex, rowIndex))
                }

                board.add(columnIndex, column.toList())
            }
            return board.toList()
        }
    }
}