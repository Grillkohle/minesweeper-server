package minesweeper.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import minesweeper.repository.GameRepository
import minesweeper.util.GameGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertNotNull

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
    fun `ensure that game is generated, saved, mapped`() {
        val gameEntity = GameGenerator.generateGameEntity(10, 10, 10)
        every { gameFactory.createGame(10, 10, 10) } returns gameEntity
        every { gameRepository.saveGame(gameEntity) } returns Unit
        every { gameMapper.toGameResponse(gameEntity) } returns GameGenerator.generateGameResponse()

        assertNotNull(gameService.createGame(10, 10, 10))

        verify { gameFactory.createGame(10, 10, 10) }
        verify { gameRepository.saveGame(gameEntity) }
        verify { gameMapper.toGameResponse(gameEntity) }
    }
}