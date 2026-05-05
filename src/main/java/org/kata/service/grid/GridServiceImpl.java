package org.kata.service.grid;

import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.entity.ProbeEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.kata.service.obstacle.ObstacleResponse;
import org.kata.service.probe.PositionResponse;
import org.kata.service.probe.ProbeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GridServiceImpl implements GridService {

    private final GridRepository gridRepository;
    private final ObstacleRepository obstacleRepository;
    private final ProbeRepository probeRepository;

    public GridServiceImpl(GridRepository gridRepository, ObstacleRepository obstacleRepository, ProbeRepository probeRepository) {
        this.gridRepository = gridRepository;
        this.obstacleRepository = obstacleRepository;
        this.probeRepository = probeRepository;
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
        obstacleRepository.deleteAll(obstacleRepository.findAllByGridId(id));
        probeRepository.deleteAll(probeRepository.findAllByGridId(id));
        gridRepository.deleteById(id);
    }

    @Override
    public GridStateResponse getState(Long id) {
        GridEntity entity = gridRepository.findById(id)
                .orElseThrow(() -> new GridNotFoundException(id));
        List<ObstacleResponse> obstacles = obstacleRepository.findAllByGridId(id).stream()
                .map(o -> new ObstacleResponse(o.getId(), o.getGridId(), o.getX(), o.getY()))
                .toList();
        List<ProbeResponse> probes = probeRepository.findAllByGridId(id).stream()
                .map(p -> new ProbeResponse(p.getId(), p.getGridId(), new PositionResponse(p.getX(), p.getY()), p.getDirection()))
                .toList();
        return new GridStateResponse(entity.getId(), entity.getWidth(), entity.getHeight(), obstacles, probes);
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
