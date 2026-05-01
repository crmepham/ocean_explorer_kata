package org.kata.repository;

import org.kata.entity.GridEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridRepository extends JpaRepository<GridEntity, Long> {
}
