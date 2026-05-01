package org.kata.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ObstacleRepositoryTest {

    @Autowired
    private ObstacleRepository obstacleRepository;

    @Autowired
    private GridRepository gridRepository;

    @AfterEach
    void cleanUp() {
        obstacleRepository.deleteAll();
        gridRepository.deleteAll();
    }

    @Test
    void savingAnObstacleAssignsADatabaseGeneratedIdAndPersistsValues() {
        GridEntity savedGrid = gridRepository.save(new GridEntity(10, 10));

        ObstacleEntity saved = obstacleRepository.save(new ObstacleEntity(savedGrid.getId(), 3, 4));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGridId()).isEqualTo(savedGrid.getId());
        assertThat(saved.getX()).isEqualTo(3);
        assertThat(saved.getY()).isEqualTo(4);
    }

    @Test
    void findingAnObstacleByGridIdAndPositionReturnsTheObstacle() {
        GridEntity savedGrid = gridRepository.save(new GridEntity(10, 10));
        obstacleRepository.save(new ObstacleEntity(savedGrid.getId(), 3, 4));

        assertThat(obstacleRepository.findByGridIdAndXAndY(savedGrid.getId(), 3, 4))
                .isPresent()
                .hasValueSatisfying(obstacle -> {
                    assertThat(obstacle.getX()).isEqualTo(3);
                    assertThat(obstacle.getY()).isEqualTo(4);
                });
    }

    @Test
    void findingAnObstacleAtAPositionWithNoObstacleReturnsEmpty() {
        GridEntity savedGrid = gridRepository.save(new GridEntity(10, 10));

        assertThat(obstacleRepository.findByGridIdAndXAndY(savedGrid.getId(), 3, 4)).isEmpty();
    }
}
