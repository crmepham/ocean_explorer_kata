package org.kata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ObstacleOutsideGridException;
import org.kata.exception.ProbeAtPositionException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.kata.service.obstacle.ObstacleRequest;
import org.kata.service.obstacle.ObstacleResponse;
import org.kata.service.obstacle.ObstacleServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObstacleServiceTest {

    @Mock
    private ObstacleRepository obstacleRepository;

    @Mock
    private GridRepository gridRepository;

    @Mock
    private ProbeRepository probeRepository;

    @InjectMocks
    private ObstacleServiceImpl obstacleService;

    @Test
    void placingAnObstacleOnAnEmptyGridCellSavesAndReturnsObstacleDetails() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));
        when(probeRepository.existsByGridIdAndXAndY(1L, 3, 4)).thenReturn(false);

        ObstacleEntity saved = new ObstacleEntity(1L, 3, 4);
        ReflectionTestUtils.setField(saved, "id", 1L);
        when(obstacleRepository.save(any())).thenReturn(saved);

        ObstacleResponse response = obstacleService.place(1L, new ObstacleRequest(3, 4));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.gridId()).isEqualTo(1L);
        assertThat(response.x()).isEqualTo(3);
        assertThat(response.y()).isEqualTo(4);
    }

    @Test
    void placingAnObstacleOnACellAlreadyContainingAnObstacleReturnsTheExistingObstacle() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));
        when(probeRepository.existsByGridIdAndXAndY(1L, 3, 4)).thenReturn(false);
        when(obstacleRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        ObstacleEntity existing = new ObstacleEntity(1L, 3, 4);
        ReflectionTestUtils.setField(existing, "id", 1L);
        when(obstacleRepository.findByGridIdAndXAndY(1L, 3, 4)).thenReturn(Optional.of(existing));

        ObstacleResponse response = obstacleService.place(1L, new ObstacleRequest(3, 4));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.x()).isEqualTo(3);
        assertThat(response.y()).isEqualTo(4);
    }

    @Test
    void placingAnObstacleAtAProbePositionThrowsProbeAtPositionException() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));
        when(probeRepository.existsByGridIdAndXAndY(1L, 3, 4)).thenReturn(true);

        assertThatThrownBy(() -> obstacleService.place(1L, new ObstacleRequest(3, 4)))
                .isInstanceOf(ProbeAtPositionException.class);
    }

    @Test
    void placingAnObstacleOnANonExistentGridThrowsGridNotFoundException() {
        when(gridRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> obstacleService.place(99L, new ObstacleRequest(3, 4)))
                .isInstanceOf(GridNotFoundException.class);
    }

    @Test
    void placingAnObstacleOutsideGridBoundsThrowsObstacleOutsideGridException() {
        GridEntity grid = new GridEntity(10, 10);
        ReflectionTestUtils.setField(grid, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(grid));

        assertThatThrownBy(() -> obstacleService.place(1L, new ObstacleRequest(50, 50)))
                .isInstanceOf(ObstacleOutsideGridException.class);
    }
}
