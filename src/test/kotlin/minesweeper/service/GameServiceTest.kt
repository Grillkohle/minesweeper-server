package minesweeper.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.CellResponseState
import minesweeper.controller.model.CellStateTransitionRequest
import minesweeper.controller.model.GameResponseState
import minesweeper.repository.GameRepository
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import minesweeper.repository.entity.GameEntityState
import minesweeper.service.exception.GameNotModifiableException
import minesweeper.service.exception.ResourceNotFoundException
import minesweeper.util.GameGenerator
import minesweeper.util.GameImporter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class GameServiceTest {
    @MockK
    lateinit var gameFactory: GameFactory

    @MockK
    lateinit var gameRepository: GameRepository

    @MockK
    lateinit var gameMapper: GameMapper

    @InjectMockKs
    lateinit var gameService: GameService

    @Test
    fun `test that game is generated, saved, mapped`() {
        val gameEntity = GameGenerator.generateGameEntity(10, 10)
        every { gameFactory.createGame(10, 10) } returns gameEntity
        every { gameRepository.saveGame(gameEntity) } returns Unit
        every { gameMapper.toGameResponse(gameEntity) } returns GameGenerator.generateGameResponse()

        assertNotNull(gameService.createGame(10, 10))

        verify { gameFactory.createGame(10, 10) }
        verify { gameRepository.saveGame(gameEntity) }
        verify { gameMapper.toGameResponse(gameEntity) }
    }

    @Test
    fun `test cell state transition game does not exist`() {
        every { gameRepository.findGame(any()) } returns null

        assertThrows<ResourceNotFoundException> {
            gameService.updateCellState(
                UUID.randomUUID(),
                CellStateTransitionRequest(1, 1, CellResponseState.REVEALED)
            )
        }
    }

    @Test
    fun `test cell state transition cell does not exist`() {
        val gameEntity = GameGenerator.generateGameEntity(10, 10)
        every { gameRepository.findGame(gameEntity.id) } returns gameEntity

        assertThrows<ResourceNotFoundException> {
            gameService.updateCellState(
                gameEntity.id,
                CellStateTransitionRequest(11, 11, CellResponseState.REVEALED)
            )
        }
    }

    @Test
    fun `test cell state transition reveal mine game is lost`() {
        val gameEntity = GameGenerator.generateGameEntity(10, 10)

        val mine = findCellWith(gameEntity, isMine = true)

        val cellResponse = CellResponse(
            mine.horizontalIndex,
            mine.verticalIndex,
            mine.numberOfAdjacentMines,
            CellResponseState.REVEALED,
            mine.isMine
        )

        every { gameRepository.findGame(gameEntity.id) } returns gameEntity
        every { gameMapper.toCellResponse(gameEntity.board.cells[mine.horizontalIndex][mine.verticalIndex]) } returns cellResponse

        val response = gameService.updateCellState(
            gameEntity.id,
            CellStateTransitionRequest(mine.horizontalIndex, mine.verticalIndex, CellResponseState.REVEALED)
        )

        assertEquals(GameResponseState.LOSS, response.gameState)
        assertEquals(1, response.changedCells.size)

        val changedCell = response.changedCells.first()
        assertEquals(mine.horizontalIndex, changedCell.horizontalIndex)
        assertEquals(mine.verticalIndex, changedCell.verticalIndex)
        assertEquals(CellResponseState.REVEALED, changedCell.state)
        assertTrue(changedCell.isMine!!)

        verify { gameRepository.findGame(gameEntity.id) }
        verify { gameMapper.toCellResponse(gameEntity.board.cells[mine.horizontalIndex][mine.verticalIndex]) }
    }

    @Test
    fun `test cell state transition reveal non-zero adjacent mines cell`() {
        val gameEntity = GameImporter.createGameFromFile("src/test/resources/test_boards/2x2_with_2_mines")

        val nonMine = findCellWith(gameEntity, isMine = false)
        val cellResponse = CellResponse(
            nonMine.horizontalIndex,
            nonMine.verticalIndex,
            nonMine.numberOfAdjacentMines,
            CellResponseState.REVEALED,
            nonMine.isMine
        )

        every { gameRepository.findGame(gameEntity.id) } returns gameEntity
        every { gameMapper.toCellResponse(gameEntity.board.cells[nonMine.horizontalIndex][nonMine.verticalIndex]) } returns cellResponse

        val response = gameService.updateCellState(
            gameEntity.id,
            CellStateTransitionRequest(nonMine.horizontalIndex, nonMine.verticalIndex, CellResponseState.REVEALED)
        )

        assertEquals(GameResponseState.IN_PROGRESS, response.gameState)
        assertEquals(1, response.changedCells.size)

        val changedCell = response.changedCells.first()
        assertEquals(nonMine.horizontalIndex, changedCell.horizontalIndex)
        assertEquals(nonMine.verticalIndex, changedCell.verticalIndex)
        assertEquals(CellResponseState.REVEALED, changedCell.state)
        assertFalse(changedCell.isMine!!)

        verify { gameRepository.findGame(gameEntity.id) }
        verify { gameMapper.toCellResponse(gameEntity.board.cells[nonMine.horizontalIndex][nonMine.verticalIndex]) }
    }

    @Test
    fun `test cell state transition reveal zero adjacent mines cell`() {
        val gameEntity = GameImporter.createGameFromFile("src/test/resources/test_boards/3x3_with_zero_adj_cells")
        val expectedRevealedCellIndices = mutableSetOf<Pair<Int, Int>>()

        every { gameRepository.findGame(gameEntity.id) } returns gameEntity
        for (horizontalIndex in 0..1) {
            for (verticalIndex in 0..2) {
                expectedRevealedCellIndices += Pair(horizontalIndex, verticalIndex)
                val cell = gameEntity.board.cells[horizontalIndex][verticalIndex]
                every { gameMapper.toCellResponse(cell) } returns CellResponse(
                    horizontalIndex = cell.horizontalIndex,
                    verticalIndex = cell.verticalIndex,
                    isMine = cell.isMine,
                    state = CellResponseState.REVEALED
                )
            }
        }

        val response =
            gameService.updateCellState(gameEntity.id, CellStateTransitionRequest(0, 0, CellResponseState.REVEALED))
        assertEquals(GameResponseState.IN_PROGRESS, response.gameState)
        assertEquals(6, response.changedCells.size)

        for (changedCell in response.changedCells) {
            assertTrue(
                expectedRevealedCellIndices.contains(
                    Pair(
                        changedCell.horizontalIndex,
                        changedCell.verticalIndex
                    )
                )
            )
            assertEquals(CellResponseState.REVEALED, changedCell.state)
        }

        verify { gameRepository.findGame(gameEntity.id) }
        verify(exactly = 6) { gameMapper.toCellResponse(any()) }
    }

    @Test
    fun `test cell state transition reveal already revealed cell`() {
        val gameEntity = GameGenerator.generateGameEntity(2, 2)

        val nonMine = findCellWith(gameEntity, isMine = false)
        nonMine.state = CellEntity.CellEntityState.REVEALED

        every { gameRepository.findGame(gameEntity.id) } returns gameEntity

        val response = gameService.updateCellState(
            gameEntity.id,
            CellStateTransitionRequest(nonMine.horizontalIndex, nonMine.verticalIndex, CellResponseState.REVEALED)
        )

        assertEquals(GameResponseState.IN_PROGRESS, response.gameState)
        assertEquals(0, response.changedCells.size)

        verify { gameRepository.findGame(gameEntity.id) }
    }

    @Test
    fun `test cell state transition not possible game already lost`() {
        val gameEntity = GameGenerator.generateGameEntity()
        gameEntity.state = GameEntityState.LOSS

        every { gameRepository.findGame(gameEntity.id) } returns gameEntity

        assertThrows<GameNotModifiableException> {
            gameService.updateCellState(
                gameEntity.id,
                CellStateTransitionRequest(0, 0, CellResponseState.REVEALED)
            )
        }
    }

    private fun findCellWith(gameEntity: GameEntity, isMine: Boolean): CellEntity {
        return gameEntity.board.cells
            .map { column ->
                column.filter { cell -> cell.isMine == isMine }
            }
            .first { column -> column.isNotEmpty() }
            .first()
    }
}