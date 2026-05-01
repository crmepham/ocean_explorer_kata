package org.kata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.domain.Direction;
import org.kata.entity.GridEntity;
import org.kata.entity.ProbeEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.exception.ProbeOnObstacleException;
import org.kata.exception.ProbeOutsideGridException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.kata.service.probe.ProbeRequest;
import org.kata.service.probe.ProbeResponse;
import org.kata.service.probe.ProbeServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProbeServiceTest {

    @Mock
    private ProbeRepository probeRepository;

    @Mock
    private GridRepository gridRepository;

    @InjectMocks
    private ProbeServiceImpl probeService;

    @Test
    void deployingAProbeSuccessfullyPersistsAndReturnsProbeDetails() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));

        ProbeEntity savedProbe = new ProbeEntity(1L, 0, 0, Direction.NORTH);
        ReflectionTestUtils.setField(savedProbe, "id", 1L);
        when(probeRepository.save(any())).thenReturn(savedProbe);

        ProbeResponse response = probeService.deploy(1L, new ProbeRequest(0, 0, Direction.NORTH));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.gridId()).isEqualTo(1L);
        assertThat(response.position().x()).isEqualTo(0);
        assertThat(response.position().y()).isEqualTo(0);
        assertThat(response.direction()).isEqualTo(Direction.NORTH);
    }

    @Test
    void deployingAProbeToANonExistentGridThrowsGridNotFoundException() {
        when(gridRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> probeService.deploy(99L, new ProbeRequest(0, 0, Direction.NORTH)))
                .isInstanceOf(GridNotFoundException.class);
    }

    @Test
    void deployingAProbeOutsideGridBoundsThrowsProbeOutsideGridException() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));

        assertThatThrownBy(() -> probeService.deploy(1L, new ProbeRequest(50, 50, Direction.NORTH)))
                .isInstanceOf(ProbeOutsideGridException.class);
    }

    @Test
    void findingAProbeByIdReturnsItsDetails() {
        ProbeEntity probe = new ProbeEntity(1L, 2, 3, Direction.EAST);
        ReflectionTestUtils.setField(probe, "id", 1L);
        when(probeRepository.findById(1L)).thenReturn(Optional.of(probe));

        ProbeResponse response = probeService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.position().x()).isEqualTo(2);
        assertThat(response.position().y()).isEqualTo(3);
        assertThat(response.direction()).isEqualTo(Direction.EAST);
    }

    @Test
    void findingANonExistentProbeThrowsProbeNotFoundException() {
        when(probeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> probeService.findById(99L))
                .isInstanceOf(ProbeNotFoundException.class);
    }
}
