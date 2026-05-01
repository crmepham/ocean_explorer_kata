package org.kata.api;

import org.kata.service.probe.ProbeRequest;
import org.kata.service.probe.ProbeResponse;
import org.kata.service.probe.ProbeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ProbeController {

    private final ProbeService probeService;

    public ProbeController(ProbeService probeService) {
        this.probeService = probeService;
    }

    @PostMapping("/grids/{gridId}/probes")
    public ResponseEntity<ProbeResponse> deploy(@PathVariable Long gridId, @RequestBody ProbeRequest request) {
        ProbeResponse response = probeService.deploy(gridId, request);
        return ResponseEntity
                .created(URI.create("/probes/" + response.id()))
                .body(response);
    }

    @GetMapping("/probes/{id}")
    public ResponseEntity<ProbeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(probeService.findById(id));
    }
}
