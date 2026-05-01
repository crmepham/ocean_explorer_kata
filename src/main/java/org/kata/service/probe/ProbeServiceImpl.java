package org.kata.service.probe;

import org.kata.entity.GridEntity;
import org.kata.entity.ProbeEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.exception.ProbeOnObstacleException;
import org.kata.exception.ProbeOutsideGridException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.springframework.stereotype.Service;

@Service
public class ProbeServiceImpl implements ProbeService {

    private final ProbeRepository probeRepository;
    private final GridRepository gridRepository;
    private final ObstacleRepository obstacleRepository;

    public ProbeServiceImpl(ProbeRepository probeRepository, GridRepository gridRepository, ObstacleRepository obstacleRepository) {
        this.probeRepository = probeRepository;
        this.gridRepository = gridRepository;
        this.obstacleRepository = obstacleRepository;
    }

    @Override
    public ProbeResponse deploy(Long gridId, ProbeRequest request) {
        GridEntity grid = gridRepository.findById(gridId)
                .orElseThrow(() -> new GridNotFoundException(gridId));

        if (isOutsideBounds(request.x(), request.y(), grid)) {
            throw new ProbeOutsideGridException(request.x(), request.y());
        }

        if (obstacleRepository.existsByGridIdAndXAndY(gridId, request.x(), request.y())) {
            throw new ProbeOnObstacleException(request.x(), request.y());
        }

        ProbeEntity saved = probeRepository.save(
                new ProbeEntity(gridId, request.x(), request.y(), request.direction())
        );
        return toResponse(saved);
    }

    @Override
    public ProbeResponse findById(Long id) {
        return probeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProbeNotFoundException(id));
    }

    private boolean isOutsideBounds(int x, int y, GridEntity grid) {
        return x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight();
    }

    private ProbeResponse toResponse(ProbeEntity entity) {
        return new ProbeResponse(
                entity.getId(),
                entity.getGridId(),
                new PositionResponse(entity.getX(), entity.getY()),
                entity.getDirection()
        );
    }
}
