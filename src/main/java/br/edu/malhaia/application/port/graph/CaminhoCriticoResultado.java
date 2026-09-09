package br.edu.malhaia.application.port.graph;

import java.util.List;
import java.util.Map;

public record CaminhoCriticoResultado(
		Map<Long, Integer> semestreMinimoPorDisciplina,
		int totalSemestres,
		List<Long> caminhoCriticoIds
) {
}
