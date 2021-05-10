package minesweeper.repository.entity

import java.util.UUID

data class GameEntity(val id: UUID = UUID.randomUUID(),
                      val board: BoardEntity
)