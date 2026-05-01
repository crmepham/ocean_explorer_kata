package org.kata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.service.grid.GridRequest;
import org.kata.service.grid.GridResponse;
import org.kata.service.grid.GridServiceImpl;
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
class GridServiceTest {

    @Mock
    private GridRepository gridRepository;

    @Mock
    private ObstacleRepository obstacleRepository;

    @InjectMocks
    private GridServiceImpl gridService;

    @Test
    void creatingAGridPersistsAndReturnsGridDetails() {
        GridEntity savedEntity = new GridEntity(10, 10);
        ReflectionTestUtils.setField(savedEntity, "id", 1L);
        when(gridRepository.save(any())).thenReturn(savedEntity);

        GridResponse response = gridService.create(new GridRequest(10, 10, List.of()));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.width()).isEqualTo(10);
        assertThat(response.height()).isEqualTo(10);
        assertThat(response.obstacles()).isEmpty();
    }

    @Test
    void listingAllGridsReturnsMappedGridDetails() {
        GridEntity entity1 = new GridEntity(10, 10);
        ReflectionTestUtils.setField(entity1, "id", 1L);
        GridEntity entity2 = new GridEntity(5, 8);
        ReflectionTestUtils.setField(entity2, "id", 2L);
        when(gridRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<GridResponse> responses = gridService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).width()).isEqualTo(10);
        assertThat(responses.get(1).id()).isEqualTo(2L);
        assertThat(responses.get(1).width()).isEqualTo(5);
    }

    @Test
    void findingAGridByIdReturnsItsDetailsWithObstacles() {
        GridEntity entity = new GridEntity(10, 10);
        ReflectionTestUtils.setField(entity, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(entity));

        ObstacleEntity obstacle = new ObstacleEntity(1L, 3, 4);
        ReflectionTestUtils.setField(obstacle, "id", 1L);
        when(obstacleRepository.findAllByGridId(1L)).thenReturn(List.of(obstacle));

        GridResponse response = gridService.findById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.width()).isEqualTo(10);
        assertThat(response.height()).isEqualTo(10);
        assertThat(response.obstacles()).hasSize(1);
        assertThat(response.obstacles().get(0).x()).isEqualTo(3);
        assertThat(response.obstacles().get(0).y()).isEqualTo(4);
    }

    @Test
    void findingANonExistentGridThrowsNotFoundException() {
        when(gridRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gridService.findById(99L))
                .isInstanceOf(GridNotFoundException.class);
    }

    @Test
    void updatingAGridChangesItsValues() {
        GridEntity existing = new GridEntity(10, 10);
        ReflectionTestUtils.setField(existing, "id", 1L);
        when(gridRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gridRepository.save(any())).thenReturn(existing);

        GridResponse response = gridService.update(1L, new GridRequest(20, 15, List.of()));

        assertThat(response.width()).isEqualTo(20);
        assertThat(response.height()).isEqualTo(15);
    }

    @Test
    void updatingANonExistentGridThrowsNotFoundException() {
        when(gridRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gridService.update(99L, new GridRequest(20, 15, List.of())))
                .isInstanceOf(GridNotFoundException.class);
    }

    @Test
    void deletingAGridDelegatesToRepository() {
        when(gridRepository.existsById(1L)).thenReturn(true);

        gridService.delete(1L);

        verify(gridRepository).deleteById(1L);
    }

    @Test
    void deletingANonExistentGridThrowsNotFoundException() {
        when(gridRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> gridService.delete(99L))
                .isInstanceOf(GridNotFoundException.class);
    }
}
