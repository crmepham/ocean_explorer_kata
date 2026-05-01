package org.kata.service.grid;

import java.util.List;

public record GridRequest(int width, int height, List<Object> obstacles) {
}
