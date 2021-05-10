package minesweeper.repository

import minesweeper.util.GameGenerator
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameRepositoryTest {
    private val gameRepository: GameRepository = GameRepository()

    @Test
    fun testSaveGame() {
        val gameEntity = GameGenerator.generateGameEntity()

        gameRepository.saveGame(gameEntity)

        assertEquals(gameEntity, gameRepository.findGame(gameEntity.id))
        assertNull(gameRepository.findGame(UUID.randomUUID()))
    }
}