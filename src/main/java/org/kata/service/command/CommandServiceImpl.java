package org.kata.service.command;

import org.kata.domain.ExecutionSummary;
import org.kata.domain.OceanFloor;
import org.kata.domain.Position2D;
import org.kata.domain.Probe;
import org.kata.entity.GridEntity;
import org.kata.entity.ProbeEntity;
import org.kata.exception.GridNotFoundException;
import org.kata.exception.ProbeNotFoundException;
import org.kata.parser.CommandParser;
import org.kata.repository.GridRepository;
import org.kata.repository.ObstacleRepository;
import org.kata.repository.ProbeRepository;
import org.kata.service.probe.PositionResponse;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    private final ProbeRepository probeRepository;
    private final GridRepository gridRepository;
    private final ObstacleRepository obstacleRepository;

    public CommandServiceImpl(ProbeRepository probeRepository, GridRepository gridRepository, ObstacleRepository obstacleRepository) {
        this.probeRepository = probeRepository;
        this.gridRepository = gridRepository;
        this.obstacleRepository = obstacleRepository;
    }

    @Override
    public CommandResponse execute(Long probeId, CommandRequest request) {
        ProbeEntity probeEntity = probeRepository.findById(probeId)
                .orElseThrow(() -> new ProbeNotFoundException(probeId));

        GridEntity gridEntity = gridRepository.findById(probeEntity.getGridId())
                .orElseThrow(() -> new GridNotFoundException(probeEntity.getGridId()));

        Set<Position2D> obstacles = obstacleRepository.findAllByGridId(gridEntity.getId())
                .stream()
                .map(o -> new Position2D(o.getX(), o.getY()))
                .collect(Collectors.toSet());

        OceanFloor oceanFloor = new OceanFloor(gridEntity.getWidth(), gridEntity.getHeight(), obstacles);

        Probe probe = new Probe(
                new Position2D(probeEntity.getX(), probeEntity.getY()),
                probeEntity.getDirection()
        );

        var commands = CommandParser.parse(request.commands());

        ExecutionSummary summary = probe.execute(commands, oceanFloor);

        probeEntity.updatePosition(probe.position().x(), probe.position().y(), probe.direction());
        probeRepository.save(probeEntity);

        return new CommandResponse(
                probeId,
                summary.commandResult(),
                commands.size(),
                summary.commandsExecuted(),
                new PositionResponse(probe.position().x(), probe.position().y()),
                probe.direction()
        );
    }
}
