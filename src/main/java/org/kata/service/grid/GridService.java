package org.kata.service.grid;

import java.util.List;

public interface GridService {

    GridResponse create(GridRequest request);

    List<GridResponse> findAll();

    GridResponse findById(Long id);

    GridResponse update(Long id, GridRequest request);

    void delete(Long id);

    GridStateResponse getState(Long id);
}
