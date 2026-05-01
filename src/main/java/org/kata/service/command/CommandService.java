package org.kata.service.command;

public interface CommandService {

    CommandResponse execute(Long probeId, CommandRequest request);
}
