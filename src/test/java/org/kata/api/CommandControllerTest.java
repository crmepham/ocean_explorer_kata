package org.kata.api;

import org.junit.jupiter.api.Test;
import org.kata.domain.Direction;
import org.kata.exception.InvalidCommandException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.service.command.CommandRequest;
import org.kata.service.command.CommandResponse;
import org.kata.service.command.CommandResult;
import org.kata.service.command.CommandService;
import org.kata.service.probe.PositionResponse;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommandController.class)
class CommandControllerTest extends ControllerTestBase {

    @MockitoBean
    private CommandService commandService;

    @Test
    void submittingASuccessfulCommandSequenceReturns200WithSuccessResult() throws Exception {
        when(commandService.execute(eq(1L), any())).thenReturn(
                new CommandResponse(1L, CommandResult.SUCCESS, 5, 5, new PositionResponse(1, 2), Direction.EAST)
        );

        mockMvc.perform(post("/probes/1/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommandRequest("FFLRF"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.probeId").value(1))
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.commandsSubmitted").value(5))
                .andExpect(jsonPath("$.commandsExecuted").value(5))
                .andExpect(jsonPath("$.finalPosition.x").value(1))
                .andExpect(jsonPath("$.finalPosition.y").value(2))
                .andExpect(jsonPath("$.finalDirection").value("EAST"));
    }

    @Test
    void submittingCommandsBlockedByAnObstacleReturns200WithBlockedResult() throws Exception {
        when(commandService.execute(eq(1L), any())).thenReturn(
                new CommandResponse(1L, CommandResult.BLOCKED_BY_OBSTACLE, 5, 2, new PositionResponse(0, 1), Direction.NORTH)
        );

        mockMvc.perform(post("/probes/1/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommandRequest("FFFFF"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("BLOCKED_BY_OBSTACLE"))
                .andExpect(jsonPath("$.commandsSubmitted").value(5))
                .andExpect(jsonPath("$.commandsExecuted").value(2))
                .andExpect(jsonPath("$.finalPosition.x").value(0))
                .andExpect(jsonPath("$.finalPosition.y").value(1));
    }

    @Test
    void submittingCommandsToANonExistentProbeReturns404() throws Exception {
        when(commandService.execute(eq(99L), any())).thenThrow(new ProbeNotFoundException(99L));

        mockMvc.perform(post("/probes/99/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommandRequest("F"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void submittingAnInvalidCommandCharacterReturns400() throws Exception {
        when(commandService.execute(eq(1L), any())).thenThrow(new InvalidCommandException('X', 3));

        mockMvc.perform(post("/probes/1/commands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommandRequest("FFXRF"))))
                .andExpect(status().isBadRequest());
    }
}
