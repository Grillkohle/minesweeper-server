package minesweeper.service

import minesweeper.controller.model.GameResponse
import minesweeper.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(
        private val gameFactory: GameFactory,
        private val gameMapper: GameMapper,
        private val gameRepository: GameRepository,
) {
    fun createGame(horizontalSize: Int,
                   verticalSize: Int): GameResponse {
        val gameEntity = gameFactory.createGame(horizontalSize, verticalSize)
        gameRepository.saveGame(gameEntity)

        return gameMapper.toGameResponse(gameEntity)
    }
}