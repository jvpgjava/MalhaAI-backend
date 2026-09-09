package br.edu.malhaia.application.graph;

import br.edu.malhaia.application.port.graph.MenorCaminhoPort;
import br.edu.malhaia.domain.model.GrafoCurricular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Dijkstra literal (peso 1) multi-fonte no sentido pré-requisito → disciplina.
 * Retorna a sequência da origem efetiva até o destino.
 * Se o destino já está nas origens, retorna só o destino.
 * Se impossível, retorna lista vazia.
 */
public class DijkstraMenorCaminho implements MenorCaminhoPort {

	private record NoDistancia(Long id, int distancia) {
	}

	@Override
	public List<Long> caminho(GrafoCurricular grafo, Set<Long> origens, Long destino) {
		Objects.requireNonNull(destino, "destino");
		if (origens == null || origens.isEmpty()) {
			return List.of();
		}
		if (!grafo.getNos().containsKey(destino)) {
			return List.of();
		}
		if (origens.contains(destino)) {
			return List.of(destino);
		}

		Map<Long, Integer> dist = new HashMap<>();
		Map<Long, Long> pred = new HashMap<>();
		PriorityQueue<NoDistancia> pq = new PriorityQueue<>(Comparator.comparingInt(NoDistancia::distancia));

		for (Long origem : origens) {
			if (!grafo.getNos().containsKey(origem)) {
				continue;
			}
			dist.put(origem, 0);
			pq.add(new NoDistancia(origem, 0));
		}

		if (dist.isEmpty()) {
			return List.of();
		}

		Set<Long> visitados = new HashSet<>();
		while (!pq.isEmpty()) {
			NoDistancia atual = pq.poll();
			if (!visitados.add(atual.id())) {
				continue;
			}
			if (atual.id().equals(destino)) {
				break;
			}
			int distAtual = dist.getOrDefault(atual.id(), Integer.MAX_VALUE);
			for (Long vizinho : grafo.getAdjacencia().getOrDefault(atual.id(), Set.of())) {
				int novaDist = distAtual + 1;
				if (novaDist < dist.getOrDefault(vizinho, Integer.MAX_VALUE)) {
					dist.put(vizinho, novaDist);
					pred.put(vizinho, atual.id());
					pq.add(new NoDistancia(vizinho, novaDist));
				}
			}
		}

		if (!dist.containsKey(destino)) {
			return List.of();
		}

		List<Long> caminho = new ArrayList<>();
		Long cursor = destino;
		while (cursor != null) {
			caminho.add(cursor);
			if (origens.contains(cursor)) {
				break;
			}
			cursor = pred.get(cursor);
		}
		Collections.reverse(caminho);
		return List.copyOf(caminho);
	}
}
