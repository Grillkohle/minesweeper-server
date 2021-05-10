package minesweeper.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameFactoryTest {
    private val gameFactory = GameFactory()

    @Test
    fun `test that board is generated correctly and each cell has the correct indices`() {
        val sizeHorizontal = 10
        val sizeVertical = 5

        val gameEntity = gameFactory.createGame(sizeHorizontal, sizeVertical, 10)

        assertNotNull(gameEntity.id)
        assertEquals(Pair(sizeHorizontal, sizeVertical), Pair(gameEntity.board.sizeHorizontal, gameEntity.board.sizeVertical))
        val board = gameEntity.board.board

        for (horizontalIndex in board.indices) {
            for (verticalIndex in board.first().indices) {
                assertEquals(horizontalIndex, board[horizontalIndex][verticalIndex].indexHorizontal)
                assertEquals(verticalIndex, board[horizontalIndex][verticalIndex].indexVertical)
            }
        }
    }
}