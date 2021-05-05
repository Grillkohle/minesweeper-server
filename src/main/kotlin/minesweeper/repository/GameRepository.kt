package minesweeper.repository

import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class GameRepository {
    var gameMap: MutableMap<UUID, GameEntity> = HashMap()

    fun saveGame(game: GameEntity){
        gameMap[game.id] = game
    }
    
    fun findGame(id: UUID): GameEntity? {
        return gameMap[id]
    }
}