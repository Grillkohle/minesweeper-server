package minesweeper.repository.entity

import java.util.UUID

data class GameEntity(val id: UUID = UUID.randomUUID(),
                      var state: GameEntityState = GameEntityState.IN_PROGRESS,
                      val board: BoardEntity
)

enum class GameEntityState {
    IN_PROGRESS,
    LOSS
}
