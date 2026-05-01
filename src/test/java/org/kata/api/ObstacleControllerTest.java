package org.kata.api;

import org.junit.jupiter.api.Test;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ObstacleOutsideGridException;
import org.kata.exception.ProbeAtPositionException;
import org.kata.service.obstacle.ObstacleRequest;
import org.kata.service.obstacle.ObstacleResponse;
import org.kata.service.obstacle.ObstacleService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ObstacleController.class)
class ObstacleControllerTest extends ControllerTestBase {

    @MockitoBean
    private ObstacleService obstacleService;

    @Test
    void placingAnObstacleOnAnEmptyGridCellReturns201WithLocationAndDetails() throws Exception {
        when(obstacleService.place(eq(1L), any()))
                .thenReturn(new ObstacleResponse(1L, 1L, 3, 4));

        mockMvc.perform(post("/grids/1/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(3, 4))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/grids/1/obstacles/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gridId").value(1))
                .andExpect(jsonPath("$.x").value(3))
                .andExpect(jsonPath("$.y").value(4));
    }

    @Test
    void placingAnObstacleOnACellAlreadyContainingAnObstacleReturns201() throws Exception {
        when(obstacleService.place(eq(1L), any()))
                .thenReturn(new ObstacleResponse(1L, 1L, 3, 4));

        mockMvc.perform(post("/grids/1/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(3, 4))))
                .andExpect(status().isCreated());
    }

    @Test
    void placingAnObstacleAtAProbePositionReturns400() throws Exception {
        when(obstacleService.place(eq(1L), any()))
                .thenThrow(new ProbeAtPositionException(3, 4));

        mockMvc.perform(post("/grids/1/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(3, 4))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placingAnObstacleOnANonExistentGridReturns404() throws Exception {
        when(obstacleService.place(eq(99L), any()))
                .thenThrow(new GridNotFoundException(99L));

        mockMvc.perform(post("/grids/99/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(3, 4))))
                .andExpect(status().isNotFound());
    }

    @Test
    void placingAnObstacleWithNegativeCoordinatesReturns400() throws Exception {
        mockMvc.perform(post("/grids/1/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(-1, 4))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void placingAnObstacleOutsideGridBoundsReturns422() throws Exception {
        when(obstacleService.place(eq(1L), any()))
                .thenThrow(new ObstacleOutsideGridException(50, 50));

        mockMvc.perform(post("/grids/1/obstacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ObstacleRequest(50, 50))))
                .andExpect(status().isUnprocessableEntity());
    }
}
