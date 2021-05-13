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

        assertEquals(gameEntity.board.horizontalSize, boardResponse.horizontalSize)
        assertEquals(gameEntity.board.verticalSize, boardResponse.verticalSize)

        for (column in boardResponse.cells.indices) {
            for (row in boardResponse.cells[column].indices) {
                val cellEntity = gameEntity.board.cells[column][row]
                val cellResponse = boardResponse.cells[column][row]

                assertEquals(cellEntity.horizontalIndex, cellResponse.horizontalIndex)
                assertEquals(cellEntity.verticalIndex, cellResponse.verticalIndex)
                assertEquals(cellEntity.isMine, cellResponse.isMine)
            }
        }
    }
}