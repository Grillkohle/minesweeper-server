package minesweeper.service

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service

@Service
class GameFactory {
    fun createGame(sizeHorizontal: Int,
                   sizeVertical: Int): GameEntity {
        val boardEntity = BoardEntity(sizeHorizontal = sizeHorizontal,
                sizeVertical = sizeVertical,
                board = List(sizeHorizontal) { horizontalIndex -> createColumn(horizontalIndex, sizeVertical) })

        return GameEntity(board = boardEntity)
    }

    private fun createColumn(horizontalIndex: Int, sizeVertical: Int): List<CellEntity> {
        return List(sizeVertical) { verticalIndex ->
            CellEntity(indexHorizontal = horizontalIndex,
                    indexVertical = verticalIndex)
        }
    }
}