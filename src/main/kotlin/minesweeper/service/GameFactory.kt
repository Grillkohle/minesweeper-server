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
        val mineIndices = generateMineIndices(horizontalSize, verticalSize)
        val boardEntity = BoardEntity(
                horizontalSize = horizontalSize,
                verticalSize = verticalSize,
                cells = List(horizontalSize) { horizontalIndex -> createColumn(
                        horizontalIndex = horizontalIndex, 
                        boardSize = horizontalSize to verticalSize, 
                        mineIndices = mineIndices) },
                numberOfMines = mineIndices.size
        )

        return GameEntity(board = boardEntity)
    }

    private fun generateMineIndices(horizontalSize: Int, verticalSize: Int): Set<Pair<Int, Int>> {
        val targetNumberOfMines = sqrt((horizontalSize * verticalSize).toDouble()).toInt()
        val placedMinesIndices: MutableSet<Pair<Int, Int>> = mutableSetOf()

        while (placedMinesIndices.size < targetNumberOfMines)
            placedMinesIndices.add(
                    Random.nextInt(0, horizontalSize - 1) to Random.nextInt(0, verticalSize - 1))

        return placedMinesIndices.toSet()
    }

    private fun createColumn(horizontalIndex: Int, boardSize: Pair<Int, Int>, mineIndices: Set<Pair<Int, Int>>): List<CellEntity> {
        val (horizontalSize, verticalSize) = boardSize
        return List(verticalSize) { verticalIndex ->
            CellEntity(
                    horizontalIndex = horizontalIndex,
                    verticalIndex = verticalIndex,
                    isMine = mineIndices.contains(horizontalIndex to verticalIndex),
                    numberOfAdjacentMines = CellEntity.getNeighborCoordinates(
                            coordinates = horizontalIndex to verticalIndex, 
                            maxCoordinates = horizontalSize - 1 to verticalSize - 1)
                            .filter { mineIndices.contains(it) }
                            .count())
        }
    }
}