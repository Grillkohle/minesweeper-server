package minesweeper.service

import minesweeper.controller.model.BoardResponse
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.CellResponseState
import minesweeper.controller.model.GameResponse
import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.CellEntity.CellEntityState
import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class GameMapper {
    companion object {
        private val enumTranslations = mapOf(
                CellEntityState.CONCEALED to CellResponseState.CONCEALED
        )

        fun resolveCellResponseState(state: CellEntityState): CellResponseState{
            return enumTranslations[state] ?: throw RuntimeException("Incomplete status mapping!")
        }
    }
    fun toGameResponse(gameEntity: GameEntity): GameResponse {
        return GameResponse(
                id = gameEntity.id,
                board = toBoardResponse(gameEntity.board)
        )
    }

    private fun toBoardResponse(boardEntity: BoardEntity): BoardResponse {
        return BoardResponse(
                horizontalSize = boardEntity.horizontalSize,
                verticalSize = boardEntity.verticalSize,
                cells = boardEntity.cells.map { column -> column.map { cell -> toCellResponse(cell) } },
                numberOfMines = boardEntity.numberOfMines
        )
    }

    private fun toCellResponse(cellEntity: CellEntity): CellResponse {
        return CellResponse(
                horizontalIndex = cellEntity.horizontalIndex,
                verticalIndex = cellEntity.verticalIndex,
                state = resolveCellResponseState(cellEntity.state)
        )
    }
}