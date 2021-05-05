package minesweeper.service

import minesweeper.controller.model.GameResponse
import minesweeper.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService (
    val gameFactory: GameFactory,
    val gameRepository: GameRepository
) {
    fun createGame(sizeHorizonal: Int,
                   sizeVertical: Int,
                   mines: Int
    ): GameResponse {
        TODO()
    }
}