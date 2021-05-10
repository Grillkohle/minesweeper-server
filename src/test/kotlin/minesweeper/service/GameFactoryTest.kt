package minesweeper.service

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
        val board = gameEntity.board.board

        for (horizontalIndex in board.indices) {
            for (verticalIndex in board.first().indices) {
                assertEquals(horizontalIndex, board[horizontalIndex][verticalIndex].horizontalIndex)
                assertEquals(verticalIndex, board[horizontalIndex][verticalIndex].verticalIndex)
            }
        }
    }
}