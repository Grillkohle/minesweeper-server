package minesweeper.service

import minesweeper.repository.entity.CellEntity
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameFactoryTest {
    private val gameFactory = GameFactory()

    @Test
    fun `test that board is generated correctly and each cell has the correct indices`() {
        val horizontalSize = 10
        val verticalSize = 5

        val gameEntity = gameFactory.createGame(horizontalSize, verticalSize)

        assertNotNull(gameEntity.id)
        assertEquals(Pair(horizontalSize, verticalSize), Pair(gameEntity.board.horizontalSize, gameEntity.board.verticalSize))
        val board = gameEntity.board.cells

        for (horizontalIndex in board.indices) {
            for (verticalIndex in board.first().indices) {
                assertEquals(horizontalIndex, board[horizontalIndex][verticalIndex].horizontalIndex)
                assertEquals(verticalIndex, board[horizontalIndex][verticalIndex].verticalIndex)
            }
        }
    }

    @Test
    fun `test that board has the correct number of mines`() {
        val horizontalSize = 10
        val verticalSize = 10
        val expectedMines = 10 // sqrt horizontalSize * verticalSize

        val gameEntity = gameFactory.createGame(horizontalSize, verticalSize)

        val actualNumberOfMines =
                gameEntity.board.cells.map { column ->
                    column.filter { cell -> cell.isMine }
                            .count()
                }.sum()

        assertEquals(expectedMines, actualNumberOfMines)
    }

    @Test
    fun `test that the number of adjacent mines is set correctly`() {
        val horizontalSize = 10
        val verticalSize = 10

        val gameEntity = gameFactory.createGame(horizontalSize, verticalSize)
        val cells = gameEntity.board.cells

        cells.forEach { column ->
            column.forEach { cell ->
                val expectedNeighboringMines =
                        CellEntity.getNeighborCoordinates(
                                coordinates = cell.horizontalIndex to cell.verticalIndex,
                                maxCoordinates = horizontalSize - 1 to verticalSize - 1)
                                .map { (x, y) -> cells[x][y] }
                                .count { it.isMine }
                assertEquals(expectedNeighboringMines, cell.numberOfAdjacentMines)
            }
        }
    }
}