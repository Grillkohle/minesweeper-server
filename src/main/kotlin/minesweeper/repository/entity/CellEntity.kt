package minesweeper.repository.entity

data class CellEntity(val horizontalIndex: Int,
                      val verticalIndex: Int,
                      var isMine : Boolean = false)
