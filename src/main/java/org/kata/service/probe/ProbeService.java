package org.kata.service.probe;

public interface ProbeService {

    ProbeResponse deploy(Long gridId, ProbeRequest request);

    ProbeResponse findById(Long id);
}
