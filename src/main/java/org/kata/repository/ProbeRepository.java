package org.kata.repository;

import org.kata.entity.ProbeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProbeRepository extends JpaRepository<ProbeEntity, Long> {

    boolean existsByGridIdAndXAndY(Long gridId, int x, int y);

    List<ProbeEntity> findAllByGridId(Long gridId);
}
