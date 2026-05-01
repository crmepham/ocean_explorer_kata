package org.kata.service.grid;

import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.service.obstacle.ObstacleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GridServiceImpl implements GridService {

    private final GridRepository gridRepository;
    private final ObstacleRepository obstacleRepository;

    public GridServiceImpl(GridRepository gridRepository, ObstacleRepository obstacleRepository) {
        this.gridRepository = gridRepository;
        this.obstacleRepository = obstacleRepository;
    }

    @Override
    public GridResponse create(GridRequest request) {
        GridEntity saved = gridRepository.save(new GridEntity(request.width(), request.height()));
        return toResponse(saved);
    }

    @Override
    public List<GridResponse> findAll() {
        return gridRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public GridResponse findById(Long id) {
        return gridRepository.findById(id)
                .map(entity -> toResponse(entity, obstacleRepository.findAllByGridId(id)))
                .orElseThrow(() -> new GridNotFoundException(id));
    }

    @Override
    public GridResponse update(Long id, GridRequest request) {
        GridEntity entity = gridRepository.findById(id)
                .orElseThrow(() -> new GridNotFoundException(id));
        entity.updateDimensions(request.width(), request.height());
        return toResponse(gridRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!gridRepository.existsById(id)) {
            throw new GridNotFoundException(id);
        }
        gridRepository.deleteById(id);
    }

    private GridResponse toResponse(GridEntity entity) {
        return toResponse(entity, List.of());
    }

    private GridResponse toResponse(GridEntity entity, List<ObstacleEntity> obstacleEntities) {
        List<ObstacleResponse> obstacles = obstacleEntities.stream()
                .map(o -> new ObstacleResponse(o.getId(), o.getGridId(), o.getX(), o.getY()))
                .toList();
        return new GridResponse(entity.getId(), entity.getWidth(), entity.getHeight(), obstacles);
    }
}
