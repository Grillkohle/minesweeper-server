package minesweeper.repository.entity

import java.util.UUID

data class GameEntity(val id: UUID,
                      val board: BoardEntity
)