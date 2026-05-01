package org.kata.service.obstacle;

import org.kata.entity.GridEntity;
import org.kata.entity.ObstacleEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ObstacleOutsideGridException;
import org.kata.exception.ProbeAtPositionException;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ObstacleServiceImpl implements ObstacleService {

    private final ObstacleRepository obstacleRepository;
    private final GridRepository gridRepository;
    private final ProbeRepository probeRepository;

    public ObstacleServiceImpl(ObstacleRepository obstacleRepository,
                                GridRepository gridRepository,
                                ProbeRepository probeRepository) {
        this.obstacleRepository = obstacleRepository;
        this.gridRepository = gridRepository;
        this.probeRepository = probeRepository;
    }

    @Override
    public ObstacleResponse place(Long gridId, ObstacleRequest request) {
        GridEntity grid = gridRepository.findById(gridId)
                .orElseThrow(() -> new GridNotFoundException(gridId));

        if (isOutsideBounds(request.x(), request.y(), grid)) {
            throw new ObstacleOutsideGridException(request.x(), request.y());
        }

        if (probeRepository.existsByGridIdAndXAndY(gridId, request.x(), request.y())) {
            throw new ProbeAtPositionException(request.x(), request.y());
        }

        try {
            return toResponse(obstacleRepository.save(new ObstacleEntity(gridId, request.x(), request.y())));
        } catch (DataIntegrityViolationException e) {
            return obstacleRepository.findByGridIdAndXAndY(gridId, request.x(), request.y())
                    .map(this::toResponse)
                    .orElseThrow(() -> e);
        }
    }

    private boolean isOutsideBounds(int x, int y, GridEntity grid) {
        return x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight();
    }

    private ObstacleResponse toResponse(ObstacleEntity entity) {
        return new ObstacleResponse(entity.getId(), entity.getGridId(), entity.getX(), entity.getY());
    }
}
