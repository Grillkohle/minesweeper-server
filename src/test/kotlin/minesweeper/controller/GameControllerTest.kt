package minesweeper.controller

import minesweeper.service.GameService
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

@WebMvcTest(controllers = [GameController::class])
class GameControllerTest(@Autowired val mockMvc: MockMvc) {
    @MockBean
    lateinit var gameService: GameService

    @Test
    fun `create game expect created 201`() {
        val expectedResponse = GameGenerator.generateGameResponse()

        `when`(gameService.createGame(anyInt(), anyInt(), anyInt())).thenReturn(expectedResponse)

        mockMvc.perform(
                request(HttpMethod.POST, "/games")
                        .queryParam("size_horizontal", "10")
                        .queryParam("size_vertical", "10")
                        .queryParam("mines", "10")
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, containsString("/games/${expectedResponse.id}")))
    }

    @Test
    fun `create game missing query parameters expect 400`() {
        mockMvc.perform(
                request(HttpMethod.POST, "/games")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `create game invalid query parameters expect 400`() {
        mockMvc.perform(
                request(HttpMethod.POST, "/games")
                        .queryParam("size_horizontal", "-1")
                        .queryParam("size_vertical", "-1")
                        .queryParam("mines", "-1")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}