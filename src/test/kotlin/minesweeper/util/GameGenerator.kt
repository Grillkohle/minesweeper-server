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
        fun generateGameEntity(horizontalSize: Int = Random.nextInt(1, 10),
                               verticalSize: Int = Random.nextInt(1, 10)): GameEntity {
            val boardEntity = BoardEntity(
                    horizontalSize = horizontalSize,
                    verticalSize = verticalSize,
                    board = generateBoardEntityArray(horizontalSize, verticalSize))

            return GameEntity(board = boardEntity)
        }

        private fun generateBoardEntityArray(horizontalSize: Int,
                                             verticalSize: Int): List<List<CellEntity>> {
            val board = mutableListOf<List<CellEntity>>()

            for (columnIndex in 0..horizontalSize) {
                val column = mutableListOf<CellEntity>()

                for (rowIndex in 0..verticalSize) {
                    column.add(rowIndex, CellEntity(columnIndex, rowIndex))
                }

                board.add(columnIndex, column.toList())
            }
            return board.toList()
        }

        fun generateGameResponse(horizontalSize: Int = Random.nextInt(1, 10),
                                 verticalSize: Int = Random.nextInt(1, 10)): GameResponse {
            val boardResponse = BoardResponse(
                    horizontalSize = horizontalSize,
                    verticalSize = verticalSize,
                    board = generateBoardResponseArray(horizontalSize, verticalSize))

            return GameResponse(
                    id = UUID.randomUUID(),
                    board = boardResponse)
        }

        private fun generateBoardResponseArray(horizontalSize: Int,
                                               verticalSize: Int): List<List<CellResponse>> {
            val board = mutableListOf<List<CellResponse>>()

            for (columnIndex in 0..horizontalSize) {
                val column = mutableListOf<CellResponse>()

                for (rowIndex in 0..verticalSize) {
                    column.add(rowIndex, CellResponse(columnIndex, rowIndex))
                }

                board.add(columnIndex, column.toList())
            }
            return board.toList()
        }
    }
}