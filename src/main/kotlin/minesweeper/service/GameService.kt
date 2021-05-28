package minesweeper.service

import minesweeper.controller.model.CellResponseState
import minesweeper.controller.model.CellStateTransitionRequest
import minesweeper.controller.model.CellStateTransitionResponse
import minesweeper.controller.model.GameResponse
import minesweeper.controller.model.GameResponseState
import minesweeper.repository.GameRepository
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import minesweeper.repository.entity.GameEntityState
import minesweeper.service.exception.CellNotModifiableException
import minesweeper.service.exception.GameNotModifiableException
import minesweeper.service.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GameService(
    private val gameFactory: GameFactory,
    private val gameMapper: GameMapper,
    private val gameRepository: GameRepository,
) {
    fun createGame(
        horizontalSize: Int,
        verticalSize: Int
    ): GameResponse {
        return gameFactory.createGame(horizontalSize, verticalSize)
            .also { gameRepository.saveGame(it) }
            .let { gameMapper.toGameResponse(it) }
    }

    fun updateCellState(
        gameId: UUID,
        transitionRequest: CellStateTransitionRequest
    ): CellStateTransitionResponse {
        val game = gameRepository.findGame(gameId)
            ?: throw ResourceNotFoundException("Game with ID $gameId does not exist!")

        if (game.state != GameEntityState.IN_PROGRESS)
            throw GameNotModifiableException("Game ${game.id} can not be modified, it is already in ${game.state} state.")

        val horizontalIndex = transitionRequest.horizontalIndex!!
        val verticalIndex = transitionRequest.verticalIndex!!
        val cell = game.board.cells
            .getOrNull(horizontalIndex)
            ?.getOrNull(verticalIndex)
            ?: throw ResourceNotFoundException("Cell with coordinates $horizontalIndex, $verticalIndex does not exist!")

        return when (transitionRequest.state) {
            CellResponseState.REVEALED -> revealCell(game, cell)
            CellResponseState.FLAGGED -> flagCell(game, cell)
            CellResponseState.CONCEALED -> concealCell(game, cell)
        }
    }

    private fun revealCell(
        gameEntity: GameEntity,
        cellEntity: CellEntity
    ): CellStateTransitionResponse {
        return when (cellEntity.state) {
            CellEntity.CellEntityState.REVEALED -> changeGameStateAndBuildTransitionResponse(gameEntity, emptySet())

            CellEntity.CellEntityState.FLAGGED -> throw CellNotModifiableException(
                "Failed to reveal cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not reveal cells which are flagged."
            )

            CellEntity.CellEntityState.CONCEALED -> {
                if (cellEntity.isMine || cellEntity.numberOfAdjacentMines > 0) {
                    cellEntity.state = CellEntity.CellEntityState.REVEALED
                    return changeGameStateAndBuildTransitionResponse(gameEntity, setOf(cellEntity))
                }

                val visitedCells = mutableSetOf<CellEntity>()
                revealCellAndNeighbors(
                    cellEntity,
                    gameEntity,
                    visitedCells
                )

                changeGameStateAndBuildTransitionResponse(gameEntity, visitedCells)
            }
        }
    }

    private fun revealCellAndNeighbors(
        cellEntity: CellEntity,
        gameEntity: GameEntity,
        visitedCells: MutableSet<CellEntity>
    ) {
        if (visitedCells.contains(cellEntity))
            return

        visitedCells += cellEntity
        cellEntity.state = CellEntity.CellEntityState.REVEALED

        if (cellEntity.numberOfAdjacentMines > 0)
            return

        CellEntity.getNeighbors(cellEntity, gameEntity.board)
            .filter { it.state == CellEntity.CellEntityState.CONCEALED }
            .forEach { neighbor -> revealCellAndNeighbors(neighbor, gameEntity, visitedCells) }
    }

    private fun flagCell(gameEntity: GameEntity, cellEntity: CellEntity): CellStateTransitionResponse {
        return when (cellEntity.state) {
            CellEntity.CellEntityState.REVEALED -> throw CellNotModifiableException(
                "Failed to flag cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not flag cells which are already revealed."
            )

            CellEntity.CellEntityState.CONCEALED -> {
                cellEntity.state = CellEntity.CellEntityState.FLAGGED
                changeGameStateAndBuildTransitionResponse(gameEntity, setOf(cellEntity))
            }

            CellEntity.CellEntityState.FLAGGED -> changeGameStateAndBuildTransitionResponse(gameEntity, emptySet())
        }
    }

    private fun concealCell(gameEntity: GameEntity, cellEntity: CellEntity): CellStateTransitionResponse {
        return when (cellEntity.state) {
            CellEntity.CellEntityState.REVEALED -> throw CellNotModifiableException(
                "Failed to conceal cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not conceal cells which are already revealed."
            )

            CellEntity.CellEntityState.CONCEALED -> changeGameStateAndBuildTransitionResponse(gameEntity, emptySet())

            CellEntity.CellEntityState.FLAGGED -> {
                cellEntity.state = CellEntity.CellEntityState.CONCEALED
                changeGameStateAndBuildTransitionResponse(gameEntity, setOf(cellEntity))
            }
        }
    }

    private fun changeGameStateAndBuildTransitionResponse(
        gameEntity: GameEntity,
        changedCells: Set<CellEntity>
    ): CellStateTransitionResponse {
        val isLost = changedCells.any { it.isMine && it.state == CellEntity.CellEntityState.REVEALED }

        if (isLost)
            gameEntity.state = GameEntityState.LOSS

        return CellStateTransitionResponse(
            gameState = if (isLost) GameResponseState.LOSS else GameResponseState.IN_PROGRESS,
            changedCells = changedCells.map { gameMapper.toCellResponse(it) }
        )
    }
}