package minesweeper.service

import minesweeper.repository.entity.BoardEntity
import minesweeper.repository.entity.CellEntity
import minesweeper.repository.entity.GameEntity
import org.springframework.stereotype.Service
import kotlin.math.sqrt
import kotlin.random.Random

@Service
class GameFactory {
    fun createGame(horizontalSize: Int,
                   verticalSize: Int): GameEntity {
        val cells = List(horizontalSize) { horizontalIndex -> createColumn(horizontalIndex, verticalSize) }
        val boardEntity = BoardEntity(
                horizontalSize = horizontalSize,
                verticalSize = verticalSize,
                cells = cells,
                numberOfMines = generateMines(cells, horizontalSize, verticalSize)
        )

        return GameEntity(board = boardEntity)
    }

    private fun generateMines(cells: List<List<CellEntity>>, horizontalSize: Int, verticalSize: Int): Int {
        val numberOfMines = sqrt((horizontalSize * verticalSize).toDouble()).toInt()
        var placedMinesCounter = 0
        while (placedMinesCounter < numberOfMines) {
            val (x, y) = Pair(
                    Random.nextInt(0, horizontalSize - 1),
                    Random.nextInt(0, verticalSize - 1))
            if (!cells[x][y].isMine) {
                cells[x][y].isMine = true
                placedMinesCounter++
            }
        }

        return numberOfMines
    }

    private fun createColumn(horizontalIndex: Int, verticalSize: Int): List<CellEntity> {
        return List(verticalSize) { verticalIndex ->
            CellEntity(horizontalIndex = horizontalIndex,
                    verticalIndex = verticalIndex)
        }
    }
}