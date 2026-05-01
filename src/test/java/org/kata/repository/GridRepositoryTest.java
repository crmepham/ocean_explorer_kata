package org.kata.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kata.entity.GridEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class GridRepositoryTest {

    @Autowired
    private GridRepository gridRepository;

    @AfterEach
    void cleanUp() {
        gridRepository.deleteAll();
    }

    @Test
    void savingAGridAssignsADatabaseGeneratedIdAndPersistsValues() {
        GridEntity saved = gridRepository.save(new GridEntity(10, 10));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getWidth()).isEqualTo(10);
        assertThat(saved.getHeight()).isEqualTo(10);
    }

    @Test
    void findingAllGridsReturnsAllSavedGrids() {
        gridRepository.save(new GridEntity(10, 10));
        gridRepository.save(new GridEntity(5, 8));

        assertThat(gridRepository.findAll()).hasSize(2);
    }

    @Test
    void findingAGridByIdReturnsTheCorrectGrid() {
        GridEntity saved = gridRepository.save(new GridEntity(10, 10));

        assertThat(gridRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(grid -> {
                    assertThat(grid.getWidth()).isEqualTo(10);
                    assertThat(grid.getHeight()).isEqualTo(10);
                });
    }

    @Test
    void deletingAGridRemovesItFromTheDatabase() {
        GridEntity saved = gridRepository.save(new GridEntity(10, 10));

        gridRepository.deleteById(saved.getId());

        assertThat(gridRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void updatingAGridPersistsTheNewValues() {
        GridEntity saved = gridRepository.save(new GridEntity(10, 10));
        saved.updateDimensions(20, 15);

        gridRepository.save(saved);

        assertThat(gridRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(grid -> {
                    assertThat(grid.getWidth()).isEqualTo(20);
                    assertThat(grid.getHeight()).isEqualTo(15);
                });
    }
}
