package org.kata.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kata.domain.Direction;
import org.kata.entity.GridEntity;
import org.kata.entity.ProbeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ProbeRepositoryTest {

    @Autowired
    private ProbeRepository probeRepository;

    @Autowired
    private GridRepository gridRepository;

    @AfterEach
    void cleanUp() {
        probeRepository.deleteAll();
        gridRepository.deleteAll();
    }

    @Test
    void savingAProbeAssignsADatabaseGeneratedIdAndPersistsValues() {
        GridEntity savedGrid = gridRepository.save(new GridEntity(10, 10));

        ProbeEntity saved = probeRepository.save(new ProbeEntity(savedGrid.getId(), 0, 0, Direction.NORTH));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGridId()).isEqualTo(savedGrid.getId());
        assertThat(saved.getX()).isEqualTo(0);
        assertThat(saved.getY()).isEqualTo(0);
        assertThat(saved.getDirection()).isEqualTo(Direction.NORTH);
    }

    @Test
    void findingAProbeByIdReturnsTheCorrectProbe() {
        GridEntity savedGrid = gridRepository.save(new GridEntity(10, 10));
        ProbeEntity saved = probeRepository.save(new ProbeEntity(savedGrid.getId(), 2, 3, Direction.EAST));

        assertThat(probeRepository.findById(saved.getId()))
                .isPresent()
                .hasValueSatisfying(probe -> {
                    assertThat(probe.getX()).isEqualTo(2);
                    assertThat(probe.getY()).isEqualTo(3);
                    assertThat(probe.getDirection()).isEqualTo(Direction.EAST);
                });
    }
}
