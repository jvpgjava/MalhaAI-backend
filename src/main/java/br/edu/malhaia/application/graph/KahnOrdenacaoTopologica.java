package br.edu.malhaia.application.graph;

import br.edu.malhaia.application.port.graph.OrdenacaoTopologicaPort;
import br.edu.malhaia.domain.exception.GrafoCiclicoException;
import br.edu.malhaia.domain.model.GrafoCurricular;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Ordenação topológica via algoritmo de Kahn.
 */
public class KahnOrdenacaoTopologica implements OrdenacaoTopologicaPort {

	@Override
	public List<Long> ordenar(GrafoCurricular grafo) {
		Map<Long, Integer> grauEntrada = new HashMap<>();
		for (Long id : grafo.getNos().keySet()) {
			grauEntrada.put(id, 0);
		}
		for (Map.Entry<Long, Set<Long>> entry : grafo.getAdjacencia().entrySet()) {
			for (Long destino : entry.getValue()) {
				grauEntrada.merge(destino, 1, Integer::sum);
			}
		}

		Queue<Long> fila = new ArrayDeque<>();
		for (Map.Entry<Long, Integer> entry : grauEntrada.entrySet()) {
			if (entry.getValue() == 0) {
				fila.add(entry.getKey());
			}
		}

		List<Long> ordenados = new ArrayList<>();
		while (!fila.isEmpty()) {
			Long atual = fila.poll();
			ordenados.add(atual);
			for (Long vizinho : grafo.getAdjacencia().getOrDefault(atual, Set.of())) {
				int novoGrau = grauEntrada.merge(vizinho, -1, Integer::sum);
				if (novoGrau == 0) {
					fila.add(vizinho);
				}
			}
		}

		if (ordenados.size() != grafo.getNos().size()) {
			throw new GrafoCiclicoException(
					"Ciclo detectado no grafo curricular: ordenação topológica incompleta");
		}
		return ordenados;
	}
}
