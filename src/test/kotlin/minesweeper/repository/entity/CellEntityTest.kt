package minesweeper.repository.entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CellEntityTest {
    @Test
    fun `test get all neighbor coordinates in middle of board`(){
        val neighborCoordinates = CellEntity.getNeighborCoordinates(5 to 5, 10 to 10)
        
        assertEquals(8, neighborCoordinates.size)
    }
    
    @Test
    fun `test get all neighbor coordinates in top left of board`(){
        val neighborCoordinates = CellEntity.getNeighborCoordinates(0 to 0, 10 to 10)

        assertEquals(3, neighborCoordinates.size)
        assertTrue { neighborCoordinates.containsAll(setOf(0 to 1, 1 to 1, 1 to 0)) }
    }

    @Test
    fun `test get all neighbor coordinates in bottom right of board`(){
        val neighborCoordinates = CellEntity.getNeighborCoordinates(9 to 9, 9 to 9)

        assertEquals(3, neighborCoordinates.size)
        assertTrue { neighborCoordinates.containsAll(setOf(8 to 8, 8 to 9, 9 to 8)) }
    }
}