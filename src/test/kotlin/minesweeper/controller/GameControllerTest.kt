package minesweeper.controller

import com.fasterxml.jackson.databind.ObjectMapper
import minesweeper.controller.model.CellResponse
import minesweeper.controller.model.CellResponseState
import minesweeper.controller.model.CellStateTransitionRequest
import minesweeper.controller.model.CellStateTransitionResponse
import minesweeper.controller.model.GameResponseState
import minesweeper.service.GameService
import minesweeper.service.exception.ResourceNotFoundException
import minesweeper.util.GameGenerator
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID

@WebMvcTest(controllers = [GameController::class])
class GameControllerTest(@Autowired val mockMvc: MockMvc,
                         @Autowired val objectMapper: ObjectMapper) {
    @MockBean
    lateinit var gameService: GameService

    @Test
    fun `create game expect created 201`() {
        val expectedResponse = GameGenerator.generateGameResponse()

        `when`(gameService.createGame(anyInt(), anyInt())).thenReturn(expectedResponse)

        mockMvc.perform(
                request(HttpMethod.POST, "/games")
                        .queryParam("horizontal_size", "10")
                        .queryParam("vertical_size", "10")
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, containsString("/games/${expectedResponse.id}")))
    }

    @Test
    fun `create game missing query parameters expect 400`() {
        mockMvc.perform(
                request(HttpMethod.POST, "/games")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `create game invalid query parameters expect 400`() {
        mockMvc.perform(
                request(HttpMethod.POST, "/games")
                        .queryParam("horizontal_size", "-1")
                        .queryParam("vertical_size", "-1")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `create game undefined runtime exception is thrown, expect 500`() {
        `when`(gameService.createGame(anyInt(), anyInt())).thenThrow(RuntimeException("OMG RUNTIME EXCEPTION"))

        mockMvc.perform(
                request(HttpMethod.POST, "/games")
                        .queryParam("horizontal_size", "10")
                        .queryParam("vertical_size", "10")
        )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `change cell state expect 200 ok`() {
        val gameId = UUID.randomUUID()
        val request = CellStateTransitionRequest(
                horizontalIndex = 0,
                verticalIndex = 0,
                state = CellResponseState.REVEALED
        )

        val response = CellStateTransitionResponse(
                gameState = GameResponseState.IN_PROGRESS,
                changedCells = listOf(
                        CellResponse(
                                horizontalIndex = 0,
                                verticalIndex = 0,
                                state = CellResponseState.REVEALED
                        )
                )
        )

        `when`(gameService.updateCellState(gameId, request)).thenReturn(response)

        mockMvc.perform(
                request(HttpMethod.PUT, "/games/${gameId}/cells/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))

        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `change cell state missing body expect 400`() {
        mockMvc.perform(
                request(HttpMethod.PUT, "/games/${UUID.randomUUID()}/cells/state")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `change cell state invalid body expect 400`() {
        val request = CellStateTransitionRequest(
                horizontalIndex = -1,
                verticalIndex = -1,
                state = CellResponseState.REVEALED
        )
        mockMvc.perform(
                request(HttpMethod.PUT, "/games/${UUID.randomUUID()}/cells/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `change cell state invalid body missing parameters expect 400`() {
        val request = mapOf("property" to "missing", "state" to "REVEALED")
        
        mockMvc.perform(
                request(HttpMethod.PUT, "/games/${UUID.randomUUID()}/cells/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Test
    fun `change cell state resource not found expect 404`() {
        val gameId = UUID.randomUUID()
        val request = CellStateTransitionRequest(
                horizontalIndex = 0,
                verticalIndex = 0,
                state = CellResponseState.REVEALED
        )

        `when`(gameService.updateCellState(gameId, request)).thenThrow(ResourceNotFoundException("Failed to find resource."))

        mockMvc.perform(
                request(HttpMethod.PUT, "/games/${gameId}/cells/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }
}