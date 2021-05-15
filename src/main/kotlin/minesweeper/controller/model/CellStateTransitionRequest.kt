package minesweeper.controller.model

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class CellStateTransitionRequest(@Min(0) val horizontalIndex: Int,
                                      @Min(0) val verticalIndex: Int,
                                      @NotBlank val state: CellResponseState
)
