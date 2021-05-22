package minesweeper.service

import minesweeper.controller.model.BoardResponse
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.CellResponseState
import minesweeper.controller.model.GameResponse
import minesweeper.controller.model.GameResponseState
import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.CellEntity.CellEntityState
import minesweeper.repository.entity.GameEntity
import minesweeper.repository.entity.GameEntityState
import org.springframework.stereotype.Service

@Service
class GameMapper {
    companion object {
        private val cellStateTranslations = mapOf(
            CellEntityState.CONCEALED to CellResponseState.CONCEALED,
            CellEntityState.REVEALED to CellResponseState.REVEALED,
            CellEntityState.FLAGGED to CellResponseState.FLAGGED
        )

        private val gameStateTranslations = mapOf(
            GameEntityState.IN_PROGRESS to GameResponseState.IN_PROGRESS,
            GameEntityState.LOSS to GameResponseState.LOSS,
        )

        fun resolveCellResponseState(state: CellEntityState): CellResponseState {
            return cellStateTranslations[state]
                ?: throw RuntimeException("Incomplete status mapping!")
        }

        fun resolveGameResponseState(state: GameEntityState): GameResponseState {
            return gameStateTranslations[state]
                ?: throw RuntimeException("Incomplete status mapping!")
        }
    }

    fun toGameResponse(gameEntity: GameEntity): GameResponse {
        return GameResponse(
            id = gameEntity.id,
            board = toBoardResponse(gameEntity.board),
            state = resolveGameResponseState(gameEntity.state)
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

    fun toCellResponse(cellEntity: CellEntity): CellResponse {
        val state = resolveCellResponseState(cellEntity.state)
        return CellResponse(
            horizontalIndex = cellEntity.horizontalIndex,
            verticalIndex = cellEntity.verticalIndex,
            state = state,
            isMine = if (state == CellResponseState.REVEALED) cellEntity.isMine else null,
            numberOfAdjacentMines = if (state == CellResponseState.REVEALED) cellEntity.numberOfAdjacentMines else null
        )
    }
}