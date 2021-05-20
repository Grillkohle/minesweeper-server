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

        val horizontalIndex = transitionRequest.horizontalIndex!!
        val verticalIndex = transitionRequest.verticalIndex!!
        val cell = game.board.cells
            .getOrNull(horizontalIndex)
            ?.getOrNull(verticalIndex)
            ?: throw ResourceNotFoundException("Cell with coordinates $horizontalIndex, $verticalIndex does not exist!")

        return when (transitionRequest.state) {
            CellResponseState.REVEALED -> revealCell(game, cell)
            else -> TODO()
        }
    }

    private fun revealCell(gameEntity: GameEntity, cell: CellEntity): CellStateTransitionResponse {
        if (cell.state == CellEntity.CellEntityState.REVEALED)
            return CellStateTransitionResponse(
                gameState = GameResponseState.IN_PROGRESS,
                changedCells = listOf()
            )

        if (cell.isMine) {
            gameEntity.state = GameEntityState.LOST
            cell.state = CellEntity.CellEntityState.REVEALED
            return CellStateTransitionResponse(
                gameState = GameResponseState.LOSS,
                changedCells = listOf(gameMapper.toCellResponse(cell))
            )
        }

        if (cell.numberOfAdjacentMines > 0) {
            cell.state = CellEntity.CellEntityState.REVEALED
            return CellStateTransitionResponse(
                gameState = GameResponseState.IN_PROGRESS,
                changedCells = listOf(gameMapper.toCellResponse(cell))
            )
        }
        val visitedCells = mutableSetOf<Pair<Int, Int>>()
        revealCellAndNeighbors(Pair(cell.horizontalIndex, cell.verticalIndex), gameEntity, visitedCells)

        return CellStateTransitionResponse(
            gameState = GameResponseState.IN_PROGRESS,
            changedCells = visitedCells
                .map { (x, y) -> gameEntity.board.cells[x][y] }
                .map { gameMapper.toCellResponse(it) }
        )
    }

    private fun revealCellAndNeighbors(
        coordinates: Pair<Int, Int>,
        gameEntity: GameEntity,
        visitedCells: MutableSet<Pair<Int, Int>>
    ) {
        if (visitedCells.contains(coordinates))
            return

        visitedCells += coordinates
        val cell = gameEntity.board.cells[coordinates.first][coordinates.second]
        cell.state = CellEntity.CellEntityState.REVEALED

        if (cell.numberOfAdjacentMines > 0)
            return

        CellEntity.getNeighborCoordinates(
            coordinates = coordinates,
            maxCoordinates = Pair(gameEntity.board.horizontalSize - 1, gameEntity.board.verticalSize - 1)
        )
            .filterNot { (x, y) -> gameEntity.board.cells[x][y].isMine }
            .forEach { neighbor -> revealCellAndNeighbors(neighbor, gameEntity, visitedCells) }
    }
}