package br.edu.malhaia.application.port.graph;

import br.edu.malhaia.domain.model.GrafoCurricular;

import java.util.List;
import java.util.Set;

public interface MenorCaminhoPort {

	/**
	 * Dijkstra multi-fonte (peso 1) no sentido pré-requisito → disciplina.
	 * Origens iniciam com distância 0.
	 */
	List<Long> caminho(GrafoCurricular grafo, Set<Long> origens, Long destino);
}
