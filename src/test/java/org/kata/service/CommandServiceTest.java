package org.kata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.domain.Direction;
import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.entity.ProbeEntity;
import org.kata.exception.InvalidCommandException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.kata.service.command.CommandRequest;
import org.kata.service.command.CommandResponse;
import org.kata.service.command.CommandResult;
import org.kata.service.command.CommandServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @Mock
    private ProbeRepository probeRepository;

    @Mock
    private GridRepository gridRepository;

    @Mock
    private ObstacleRepository obstacleRepository;

    @InjectMocks
    private CommandServiceImpl commandService;

    @Test
    void executingCommandsSuccessfullyReturnsSuccessResult() {
        ProbeEntity probeEntity = new ProbeEntity(1L, 0, 0, Direction.NORTH);
        ReflectionTestUtils.setField(probeEntity, "id", 1L);
        GridEntity gridEntity = new GridEntity(10, 10);
        ReflectionTestUtils.setField(gridEntity, "id", 1L);

        when(probeRepository.findById(1L)).thenReturn(Optional.of(probeEntity));
        when(gridRepository.findById(1L)).thenReturn(Optional.of(gridEntity));
        when(obstacleRepository.findAllByGridId(1L)).thenReturn(List.of());
        when(probeRepository.save(any())).thenReturn(probeEntity);

        CommandResponse response = commandService.execute(1L, new CommandRequest("FF"));

        assertThat(response.result()).isEqualTo(CommandResult.SUCCESS);
        assertThat(response.commandsSubmitted()).isEqualTo(2);
        assertThat(response.commandsExecuted()).isEqualTo(2);
        assertThat(response.finalPosition().x()).isEqualTo(0);
        assertThat(response.finalPosition().y()).isEqualTo(2);
        assertThat(response.finalDirection()).isEqualTo(Direction.NORTH);
        assertThat(response.probeId()).isEqualTo(1L);
        verify(probeRepository).save(probeEntity);
    }

    @Test
    void executingCommandsBlockedByObstacleReturnsBlockedResult() {
        ProbeEntity probeEntity = new ProbeEntity(1L, 0, 0, Direction.NORTH);
        ReflectionTestUtils.setField(probeEntity, "id", 1L);
        GridEntity gridEntity = new GridEntity(10, 10);
        ReflectionTestUtils.setField(gridEntity, "id", 1L);
        ObstacleEntity obstacle = new ObstacleEntity(1L, 0, 1);

        when(probeRepository.findById(1L)).thenReturn(Optional.of(probeEntity));
        when(gridRepository.findById(1L)).thenReturn(Optional.of(gridEntity));
        when(obstacleRepository.findAllByGridId(1L)).thenReturn(List.of(obstacle));
        when(probeRepository.save(any())).thenReturn(probeEntity);

        CommandResponse response = commandService.execute(1L, new CommandRequest("FFF"));

        assertThat(response.result()).isEqualTo(CommandResult.BLOCKED_BY_OBSTACLE);
        assertThat(response.commandsSubmitted()).isEqualTo(3);
        assertThat(response.commandsExecuted()).isEqualTo(0);
        assertThat(response.finalPosition().x()).isEqualTo(0);
        assertThat(response.finalPosition().y()).isEqualTo(0);
    }

    @Test
    void executingCommandsForNonExistentProbeThrowsProbeNotFoundException() {
        when(probeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandService.execute(99L, new CommandRequest("F")))
                .isInstanceOf(ProbeNotFoundException.class);
    }

    @Test
    void executingUnknownCommandCharacterThrowsInvalidCommandException() {
        ProbeEntity probeEntity = new ProbeEntity(1L, 0, 0, Direction.NORTH);
        ReflectionTestUtils.setField(probeEntity, "id", 1L);
        GridEntity gridEntity = new GridEntity(10, 10);
        ReflectionTestUtils.setField(gridEntity, "id", 1L);

        when(probeRepository.findById(1L)).thenReturn(Optional.of(probeEntity));
        when(gridRepository.findById(1L)).thenReturn(Optional.of(gridEntity));
        when(obstacleRepository.findAllByGridId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> commandService.execute(1L, new CommandRequest("FXB")))
                .isInstanceOf(InvalidCommandException.class);
    }
}
