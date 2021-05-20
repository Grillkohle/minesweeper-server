package minesweeper.service

import minesweeper.repository.entity.CellEntity
import minesweeper.util.GameGenerator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameMapperTest {
    private val gameMapper: GameMapper = GameMapper()

    @Test
    fun `test map to game response all cells concealed`() {
        val gameEntity = GameGenerator.generateGameEntity()

        val gameResponse = gameMapper.toGameResponse(gameEntity)

        assertEquals(gameEntity.id, gameResponse.id)
        assertEquals(gameEntity.state.toString(), gameResponse.state.toString())

        val boardResponse = gameResponse.board

        assertEquals(gameEntity.board.horizontalSize, boardResponse.horizontalSize)
        assertEquals(gameEntity.board.verticalSize, boardResponse.verticalSize)

        for (column in boardResponse.cells.indices) {
            for (row in boardResponse.cells[column].indices) {
                val cellEntity = gameEntity.board.cells[column][row]
                val cellResponse = boardResponse.cells[column][row]

                assertEquals(cellEntity.horizontalIndex, cellResponse.horizontalIndex)
                assertEquals(cellEntity.verticalIndex, cellResponse.verticalIndex)
                assertEquals(cellEntity.state.toString(), cellResponse.state.toString())
                assertNull(cellResponse.isMine)
            }
        }
    }

    @Test
    fun `test map cell to cell response, mine but not revealed`() {
        val cellEntity = CellEntity(
                horizontalIndex = 0,
                verticalIndex = 0,
                isMine = true,
                state = CellEntity.CellEntityState.CONCEALED
        )

        val cellResponse = gameMapper.toCellResponse(cellEntity)

        assertEquals(cellEntity.horizontalIndex, cellResponse.horizontalIndex)
        assertEquals(cellEntity.verticalIndex, cellResponse.verticalIndex)
        assertEquals(cellEntity.state.toString(), cellResponse.state.toString())
        assertNull(cellResponse.isMine)
    }

    @Test
    fun `test map cell to cell response, mine but revealed`() {
        val cellEntity = CellEntity(
                horizontalIndex = 0,
                verticalIndex = 0,
                isMine = true,
                state = CellEntity.CellEntityState.REVEALED
        )

        val cellResponse = gameMapper.toCellResponse(cellEntity)
        
        assertEquals(cellEntity.horizontalIndex, cellResponse.horizontalIndex)
        assertEquals(cellEntity.verticalIndex, cellResponse.verticalIndex)
        assertEquals(cellEntity.state.toString(), cellResponse.state.toString())
        assertEquals(cellEntity.isMine, cellResponse.isMine)
    }
}