package org.kata.api;

import org.kata.service.grid.GridRequest;
import org.kata.service.grid.GridResponse;
import org.kata.service.grid.GridService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/grids")
public class GridController {

    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @PostMapping
    public ResponseEntity<GridResponse> create(@RequestBody GridRequest request) {
        GridResponse response = gridService.create(request);
        return ResponseEntity
                .created(URI.create("/grids/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<GridResponse>> findAll() {
        return ResponseEntity.ok(gridService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GridResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(gridService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GridResponse> update(@PathVariable Long id, @RequestBody GridRequest request) {
        return ResponseEntity.ok(gridService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gridService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
