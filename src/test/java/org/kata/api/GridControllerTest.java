package org.kata.api;

import org.junit.jupiter.api.Test;
import org.kata.exception.GridNotFoundException;
import org.kata.service.grid.GridRequest;
import org.kata.service.grid.GridResponse;
import org.kata.service.grid.GridService;
import org.kata.service.obstacle.ObstacleResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GridController.class)
class GridControllerTest extends ControllerTestBase {

    @MockitoBean
    private GridService gridService;

    @Test
    void creatingAGridReturns201WithLocationHeaderAndGridDetails() throws Exception {
        when(gridService.create(any())).thenReturn(new GridResponse(1L, 10, 10, List.of()));

        mockMvc.perform(post("/grids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GridRequest(10, 10, List.of()))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/grids/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.width").value(10))
                .andExpect(jsonPath("$.height").value(10))
                .andExpect(jsonPath("$.obstacles").isEmpty());
    }

    @Test
    void listingAllGridsReturns200WithAllGridDetails() throws Exception {
        when(gridService.findAll()).thenReturn(List.of(
                new GridResponse(1L, 10, 10, List.of()),
                new GridResponse(2L, 5, 8, List.of())
        ));

        mockMvc.perform(get("/grids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].width").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].width").value(5));
    }

    @Test
    void gettingAGridByIdReturns200WithGridDetailsAndObstacles() throws Exception {
        when(gridService.findById(1L)).thenReturn(
                new GridResponse(1L, 10, 10, List.of(new ObstacleResponse(1L, 1L, 3, 4)))
        );

        mockMvc.perform(get("/grids/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.width").value(10))
                .andExpect(jsonPath("$.height").value(10))
                .andExpect(jsonPath("$.obstacles.length()").value(1))
                .andExpect(jsonPath("$.obstacles[0].x").value(3))
                .andExpect(jsonPath("$.obstacles[0].y").value(4));
    }

    @Test
    void gettingANonExistentGridReturns404() throws Exception {
        when(gridService.findById(99L)).thenThrow(new GridNotFoundException(99L));

        mockMvc.perform(get("/grids/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatingAGridReturns200WithUpdatedDetails() throws Exception {
        when(gridService.update(eq(1L), any())).thenReturn(new GridResponse(1L, 20, 15, List.of()));

        mockMvc.perform(put("/grids/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GridRequest(20, 15, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.width").value(20))
                .andExpect(jsonPath("$.height").value(15));
    }

    @Test
    void updatingANonExistentGridReturns404() throws Exception {
        when(gridService.update(eq(99L), any())).thenThrow(new GridNotFoundException(99L));

        mockMvc.perform(put("/grids/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GridRequest(20, 15, List.of()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingAGridReturns204() throws Exception {
        mockMvc.perform(delete("/grids/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletingANonExistentGridReturns404() throws Exception {
        doThrow(new GridNotFoundException(99L)).when(gridService).delete(99L);

        mockMvc.perform(delete("/grids/99"))
                .andExpect(status().isNotFound());
    }
}
