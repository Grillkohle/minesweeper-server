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
            CellEntity.CellEntityState.REVEALED -> revealFullyFlaggedCellNeighbors(gameEntity, cellEntity)

            CellEntity.CellEntityState.FLAGGED -> throw CellNotModifiableException(
                "Failed to reveal cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not reveal cells which are flagged."
            )

            CellEntity.CellEntityState.CONCEALED -> {
                if (cellEntity.isMine) {
                    gameEntity.state = GameEntityState.LOSS
                    cellEntity.state = CellEntity.CellEntityState.REVEALED
                    return CellStateTransitionResponse(
                        gameState = GameResponseState.LOSS,
                        changedCells = listOf(gameMapper.toCellResponse(cellEntity))
                    )
                }

                if (cellEntity.numberOfAdjacentMines > 0) {
                    cellEntity.state = CellEntity.CellEntityState.REVEALED
                    return CellStateTransitionResponse(changedCells = listOf(gameMapper.toCellResponse(cellEntity)))
                }
                val visitedCells = mutableSetOf<Pair<Int, Int>>()
                revealCellAndNeighbors(
                    cellEntity,
                    gameEntity,
                    visitedCells
                )

                CellStateTransitionResponse(changedCells = visitedCells
                    .map { (x, y) -> gameEntity.board.cells[x][y] }
                    .map { gameMapper.toCellResponse(it) }
                )
            }
        }
    }

    private fun revealFullyFlaggedCellNeighbors(
        gameEntity: GameEntity,
        cellEntity: CellEntity
    ): CellStateTransitionResponse {
        val neighbors = CellEntity.getNeighborCoordinates(
            cellEntity.coordinates,
            gameEntity.board.sizeCoordinates
        ).map { (x, y) -> gameEntity.board.cells[x][y] }

        return if (
            cellEntity.numberOfAdjacentMines == 0 ||
            neighbors.count { it.state == CellEntity.CellEntityState.FLAGGED } != cellEntity.numberOfAdjacentMines
        )
            CellStateTransitionResponse(changedCells = listOf())
        else {
            val changedCells = neighbors
                .filter { it.state == CellEntity.CellEntityState.CONCEALED }
                .onEach { it.state = CellEntity.CellEntityState.REVEALED }

            CellStateTransitionResponse(
                gameState = if (changedCells.any { it.isMine }) GameResponseState.LOSS else GameResponseState.IN_PROGRESS,
                changedCells = changedCells.map { gameMapper.toCellResponse(it) }
            )
        }
    }

    private fun revealCellAndNeighbors(
        cellEntity: CellEntity,
        gameEntity: GameEntity,
        visitedCells: MutableSet<Pair<Int, Int>>
    ) {
        if (visitedCells.contains(cellEntity.coordinates))
            return

        visitedCells += cellEntity.coordinates
        cellEntity.state = CellEntity.CellEntityState.REVEALED

        if (cellEntity.numberOfAdjacentMines > 0)
            return

        CellEntity.getNeighborCoordinates(
            coordinates = cellEntity.coordinates,
            maxCoordinates = gameEntity.board.sizeCoordinates
        )
            .map{ (x, y) -> gameEntity.board.cells[x][y]}
            .filterNot { it.isMine }
            .forEach { neighbor -> revealCellAndNeighbors(neighbor, gameEntity, visitedCells) }
    }

    private fun flagCell(
        gameEntity: GameEntity,
        cellEntity: CellEntity
    ): CellStateTransitionResponse {
        return when (cellEntity.state) {
            CellEntity.CellEntityState.REVEALED -> throw CellNotModifiableException(
                "Failed to flag cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not flag cells which are already revealed."
            )

            CellEntity.CellEntityState.CONCEALED -> {
                cellEntity.state = CellEntity.CellEntityState.FLAGGED
                CellStateTransitionResponse(changedCells = listOf(gameMapper.toCellResponse(cellEntity)))
            }

            CellEntity.CellEntityState.FLAGGED -> CellStateTransitionResponse(changedCells = listOf())
        }
    }

    private fun concealCell(gameEntity: GameEntity, cellEntity: CellEntity): CellStateTransitionResponse {
        return when (cellEntity.state) {
            CellEntity.CellEntityState.REVEALED -> throw CellNotModifiableException(
                "Failed to conceal cell of game ${gameEntity.id} at (${cellEntity.horizontalIndex}, ${cellEntity.verticalIndex}):" +
                        " Can not conceal cells which are already revealed."
            )

            CellEntity.CellEntityState.CONCEALED -> CellStateTransitionResponse(changedCells = listOf())

            CellEntity.CellEntityState.FLAGGED -> {
                cellEntity.state = CellEntity.CellEntityState.CONCEALED
                CellStateTransitionResponse(
                    changedCells = listOf(gameMapper.toCellResponse(cellEntity))
                )
            }
        }
    }
}