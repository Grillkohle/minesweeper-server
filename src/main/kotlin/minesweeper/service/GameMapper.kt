package minesweeper.service

import minesweeper.controller.model.BoardResponse
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.GameResponse
import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service

@Service
class GameMapper {
    fun toGameResponse(gameEntity: GameEntity): GameResponse {
        return GameResponse(
                id = gameEntity.id,
                board = toBoardResponse(gameEntity.board)
        )
    }

    private fun toBoardResponse(boardEntity: BoardEntity): BoardResponse {
        return BoardResponse(
                sizeHorizontal = boardEntity.sizeHorizontal,
                sizeVertical = boardEntity.sizeVertical,
                mines = boardEntity.mines,
                board = boardEntity.board.map { outerArray -> outerArray.map { cell -> toCellResponse(cell) } }
        )
    }

    private fun toCellResponse(cellEntity: CellEntity): CellResponse {
        return CellResponse(
                indexHorizontal = cellEntity.indexHorizontal,
                indexVertical = cellEntity.indexVertical
        )
    }
}