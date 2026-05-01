package org.kata.api;

import jakarta.validation.Valid;
import org.kata.service.obstacle.ObstacleRequest;
import org.kata.service.obstacle.ObstacleResponse;
import org.kata.service.obstacle.ObstacleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ObstacleController {

    private final ObstacleService obstacleService;

    public ObstacleController(ObstacleService obstacleService) {
        this.obstacleService = obstacleService;
    }

    @PostMapping("/grids/{gridId}/obstacles")
    public ResponseEntity<ObstacleResponse> place(@PathVariable Long gridId, @Valid @RequestBody ObstacleRequest request) {
        ObstacleResponse response = obstacleService.place(gridId, request);
        return ResponseEntity
                .created(URI.create("/grids/" + gridId + "/obstacles/" + response.id()))
                .body(response);
    }
}
