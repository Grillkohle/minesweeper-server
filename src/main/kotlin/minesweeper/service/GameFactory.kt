package minesweeper.service

import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service

@Service
class GameFactory {
    fun createGame(sizeHorizontal: Int,
                   sizeVertical: Int,
                   mines: Int
    ): GameEntity {
        TODO()
    }
}