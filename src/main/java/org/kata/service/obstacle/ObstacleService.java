package org.kata.service.obstacle;

public interface ObstacleService {

    ObstacleResponse place(Long gridId, ObstacleRequest request);
}
