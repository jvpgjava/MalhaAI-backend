package br.edu.malhaia.domain.model;

/**
 * Aresta do DAG curricular: {@code preRequisitoId} → {@code disciplinaId}.
 */
public record PreRequisito(
		Long id,
		Long disciplinaId,
		Long preRequisitoId
) {
}
