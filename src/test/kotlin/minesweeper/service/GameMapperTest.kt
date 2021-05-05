package minesweeper.service

import org.junit.jupiter.api.Test
import minesweeper.util.GameGenerator
import kotlin.test.assertEquals

class GameMapperTest {
    private val gameMapper: GameMapper = GameMapper()

    @Test
    fun testMapToGameResponse() {
        val gameEntity = GameGenerator.generateGameEntity()

        val gameResponse = gameMapper.toGameResponse(gameEntity)

        assertEquals(gameEntity.id, gameResponse.id)

        val boardResponse = gameResponse.board

        assertEquals(gameEntity.board.sizeHorizontal, boardResponse.sizeHorizontal)
        assertEquals(gameEntity.board.sizeVertical, boardResponse.sizeVertical)
        assertEquals(gameEntity.board.mines, boardResponse.mines)

        for (column in boardResponse.board.indices) {
            for (row in boardResponse.board[column].indices) {
                val cellEntity = gameEntity.board.board[column][row]
                val cellResponse = boardResponse.board[column][row]

                assertEquals(cellEntity.indexHorizontal, cellResponse.indexHorizontal)
                assertEquals(cellEntity.indexVertical, cellResponse.indexVertical)
            }
        }
    }
}