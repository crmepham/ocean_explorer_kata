package org.kata.api;

import org.kata.service.command.CommandRequest;
import org.kata.service.command.CommandResponse;
import org.kata.service.command.CommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/probes/{probeId}/commands")
    public ResponseEntity<CommandResponse> execute(@PathVariable Long probeId, @RequestBody CommandRequest request) {
        return ResponseEntity.ok(commandService.execute(probeId, request));
    }
}
