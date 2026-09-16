package br.edu.malhaia.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Grafo curricular em memória.
 * <ul>
 *   <li>{@code adjacencia}: preReqId → disciplinas que dependem dele</li>
 *   <li>{@code preRequisitos}: disciplinaId → seus pré-requisitos</li>
 * </ul>
 */
public class GrafoCurricular {

	private final Map<Long, Disciplina> nos = new HashMap<>();
	private final Map<Long, Set<Long>> adjacencia = new HashMap<>();
	private final Map<Long, Set<Long>> preRequisitos = new HashMap<>();

	public Map<Long, Disciplina> getNos() {
		return Collections.unmodifiableMap(nos);
	}

	public Map<Long, Set<Long>> getAdjacencia() {
		return Collections.unmodifiableMap(adjacencia);
	}

	public Map<Long, Set<Long>> getPreRequisitos() {
		return Collections.unmodifiableMap(preRequisitos);
	}

	public void adicionarDisciplina(Disciplina disciplina) {
		nos.put(disciplina.id(), disciplina);
		adjacencia.computeIfAbsent(disciplina.id(), id -> new HashSet<>());
		preRequisitos.computeIfAbsent(disciplina.id(), id -> new HashSet<>());
	}

	public void adicionarAresta(Long preRequisitoId, Long disciplinaId) {
		if (!nos.containsKey(preRequisitoId) || !nos.containsKey(disciplinaId)) {
			throw new IllegalArgumentException(
					"Ambos os nós devem existir no grafo antes de adicionar a aresta");
		}
		adjacencia.computeIfAbsent(preRequisitoId, id -> new HashSet<>()).add(disciplinaId);
		preRequisitos.computeIfAbsent(disciplinaId, id -> new HashSet<>()).add(preRequisitoId);
	}

	/**
	 * Retorna um novo grafo contendo apenas as disciplinas ofertadas e as arestas
	 * entre elas.
	 */
	public GrafoCurricular filtrarPorOfertadas(Set<Long> idsOfertados) {
		GrafoCurricular filtrado = new GrafoCurricular();
		for (Disciplina disciplina : nos.values()) {
			if (idsOfertados.contains(disciplina.id())) {
				filtrado.adicionarDisciplina(disciplina);
			}
		}
		for (Map.Entry<Long, Set<Long>> entry : adjacencia.entrySet()) {
			Long preReqId = entry.getKey();
			if (!idsOfertados.contains(preReqId)) {
				continue;
			}
			for (Long disciplinaId : entry.getValue()) {
				if (idsOfertados.contains(disciplinaId)) {
					filtrado.adicionarAresta(preReqId, disciplinaId);
				}
			}
		}
		return filtrado;
	}

	/**
	 * Remove disciplinas já concluídas. Pré-requisitos concluídos somem do grafo,
	 * então o que restava “travado” vira fonte do caminho restante.
	 */
	public GrafoCurricular removerConcluidas(Set<Long> concluidas) {
		if (concluidas == null || concluidas.isEmpty()) {
			return this;
		}
		GrafoCurricular restante = new GrafoCurricular();
		for (Disciplina disciplina : nos.values()) {
			if (!concluidas.contains(disciplina.id())) {
				restante.adicionarDisciplina(disciplina);
			}
		}
		for (Map.Entry<Long, Set<Long>> entry : adjacencia.entrySet()) {
			Long preReqId = entry.getKey();
			if (concluidas.contains(preReqId) || !restante.nos.containsKey(preReqId)) {
				continue;
			}
			for (Long disciplinaId : entry.getValue()) {
				if (!concluidas.contains(disciplinaId) && restante.nos.containsKey(disciplinaId)) {
					restante.adicionarAresta(preReqId, disciplinaId);
				}
			}
		}
		return restante;
	}
}
