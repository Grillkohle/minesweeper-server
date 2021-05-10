package minesweeper.repository

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameRepositoryTest {
    private val gameRepository: GameRepository = GameRepository()

    @Test
    fun testSaveGame() {

        val cellEntity = CellEntity(0, 0)
        val board = listOf(listOf(cellEntity))
        val boardEntity = BoardEntity(1, 1, board)
        val gameEntity = GameEntity(UUID.randomUUID(), boardEntity)

        gameRepository.saveGame(gameEntity)

        assertEquals(gameEntity, gameRepository.findGame(gameEntity.id))
        assertNull(gameRepository.findGame(UUID.randomUUID()))
    }
}