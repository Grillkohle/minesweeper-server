package minesweeper.controller.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CellStateTransitionRequest(@field:NotNull @field:Min(0) val horizontalIndex: Int?,
                                      @field:NotNull @field:Min(0) val verticalIndex: Int?,
                                      val state: CellResponseState
)