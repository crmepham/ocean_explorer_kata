package org.kata.repository;

import org.kata.entity.ObstacleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObstacleRepository extends JpaRepository<ObstacleEntity, Long> {

    boolean existsByGridIdAndXAndY(Long gridId, int x, int y);

    Optional<ObstacleEntity> findByGridIdAndXAndY(Long gridId, int x, int y);

    List<ObstacleEntity> findAllByGridId(Long gridId);
}
