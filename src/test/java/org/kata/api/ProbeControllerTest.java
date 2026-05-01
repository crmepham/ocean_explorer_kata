package org.kata.api;

import org.junit.jupiter.api.Test;
import org.kata.domain.Direction;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.exception.ProbeOnObstacleException;
import org.kata.exception.ProbeOutsideGridException;
import org.kata.service.probe.PositionResponse;
import org.kata.service.probe.ProbeRequest;
import org.kata.service.probe.ProbeResponse;
import org.kata.service.probe.ProbeService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProbeController.class)
class ProbeControllerTest extends ControllerTestBase {

    @MockitoBean
    private ProbeService probeService;

    @Test
    void deployingAProbeReturns201WithLocationAndProbeDetails() throws Exception {
        when(probeService.deploy(eq(1L), any())).thenReturn(
                new ProbeResponse(1L, 1L, new PositionResponse(0, 0), Direction.NORTH)
        );

        mockMvc.perform(post("/grids/1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProbeRequest(0, 0, Direction.NORTH))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/probes/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gridId").value(1))
                .andExpect(jsonPath("$.position.x").value(0))
                .andExpect(jsonPath("$.position.y").value(0))
                .andExpect(jsonPath("$.direction").value("NORTH"));
    }

    @Test
    void deployingAProbeToANonExistentGridReturns404() throws Exception {
        when(probeService.deploy(eq(99L), any())).thenThrow(new GridNotFoundException(99L));

        mockMvc.perform(post("/grids/99/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProbeRequest(0, 0, Direction.NORTH))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deployingAProbeOutsideGridBoundsReturns422() throws Exception {
        when(probeService.deploy(eq(1L), any())).thenThrow(new ProbeOutsideGridException(50, 50));

        mockMvc.perform(post("/grids/1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProbeRequest(50, 50, Direction.NORTH))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deployingAProbeOnAnObstacleReturns400() throws Exception {
        when(probeService.deploy(eq(1L), any())).thenThrow(new ProbeOnObstacleException(3, 4));

        mockMvc.perform(post("/grids/1/probes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProbeRequest(3, 4, Direction.NORTH))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gettingAProbeByIdReturns200WithProbeDetails() throws Exception {
        when(probeService.findById(1L)).thenReturn(
                new ProbeResponse(1L, 1L, new PositionResponse(2, 3), Direction.EAST)
        );

        mockMvc.perform(get("/probes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.gridId").value(1))
                .andExpect(jsonPath("$.position.x").value(2))
                .andExpect(jsonPath("$.position.y").value(3))
                .andExpect(jsonPath("$.direction").value("EAST"));
    }

    @Test
    void gettingANonExistentProbeReturns404() throws Exception {
        when(probeService.findById(99L)).thenThrow(new ProbeNotFoundException(99L));

        mockMvc.perform(get("/probes/99"))
                .andExpect(status().isNotFound());
    }
}
