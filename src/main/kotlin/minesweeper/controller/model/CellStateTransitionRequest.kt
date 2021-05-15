package minesweeper.controller.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CellStateTransitionRequest(@get:NotNull @get:Min(0) val horizontalIndex: Int,
                                      @get:NotNull @get:Min(0) val verticalIndex: Int,
                                      val state: CellResponseState
)
