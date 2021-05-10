package minesweeper.service

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service

@Service
class GameFactory {
    fun createGame(horizontalSize: Int,
                   verticalSize: Int): GameEntity {
        val boardEntity = BoardEntity(
                horizontalSize = horizontalSize,
                verticalSize = verticalSize,
                cells = List(horizontalSize) { horizontalIndex -> createColumn(horizontalIndex, verticalSize) })

        return GameEntity(board = boardEntity)
    }

    private fun createColumn(horizontalIndex: Int, verticalSize: Int): List<CellEntity> {
        return List(verticalSize) { verticalIndex ->
            CellEntity(horizontalIndex = horizontalIndex,
                    verticalIndex = verticalIndex)
        }
    }
}