package br.edu.malhaia.application.graph;

import br.edu.malhaia.application.port.graph.CaminhoCriticoPort;
import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.application.port.graph.OrdenacaoTopologicaPort;
import br.edu.malhaia.domain.model.GrafoCurricular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Caminho crítico por DP sobre ordenação topológica:
 * {@code semestreMin[v] = 1 + max(semestreMin[u] for u in preReqs)}, ou 1 se sem pré-req.
 */
public class CaminhoCriticoCalculator implements CaminhoCriticoPort {

	private final OrdenacaoTopologicaPort ordenacaoTopologica;

	public CaminhoCriticoCalculator() {
		this(new KahnOrdenacaoTopologica());
	}

	public CaminhoCriticoCalculator(OrdenacaoTopologicaPort ordenacaoTopologica) {
		this.ordenacaoTopologica = ordenacaoTopologica;
	}

	@Override
	public CaminhoCriticoResultado calcular(GrafoCurricular grafo) {
		List<Long> ordem = ordenacaoTopologica.ordenar(grafo);
		Map<Long, Integer> semestreMin = new HashMap<>();
		Map<Long, Long> predecessorCritico = new HashMap<>();

		for (Long id : ordem) {
			Set<Long> preReqs = grafo.getPreRequisitos().getOrDefault(id, Set.of());
			if (preReqs.isEmpty()) {
				semestreMin.put(id, 1);
				continue;
			}
			int maxPre = 0;
			Long melhorPre = null;
			for (Long pre : preReqs) {
				int valor = semestreMin.getOrDefault(pre, 1);
				if (valor > maxPre) {
					maxPre = valor;
					melhorPre = pre;
				}
			}
			semestreMin.put(id, maxPre + 1);
			predecessorCritico.put(id, melhorPre);
		}

		int totalSemestres = semestreMin.values().stream().mapToInt(Integer::intValue).max().orElse(0);

		Long fimCritico = null;
		for (Map.Entry<Long, Integer> entry : semestreMin.entrySet()) {
			if (entry.getValue() == totalSemestres) {
				fimCritico = entry.getKey();
				break;
			}
		}

		List<Long> caminhoCritico = new ArrayList<>();
		Long atual = fimCritico;
		while (atual != null) {
			caminhoCritico.add(atual);
			atual = predecessorCritico.get(atual);
		}
		Collections.reverse(caminhoCritico);

		return new CaminhoCriticoResultado(
				Collections.unmodifiableMap(semestreMin),
				totalSemestres,
				List.copyOf(caminhoCritico)
		);
	}
}
