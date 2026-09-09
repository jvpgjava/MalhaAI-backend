package br.edu.malhaia.domain.model;

public record ConflitoHorario(
		Long id,
		Long disciplinaAId,
		Long disciplinaBId,
		String semestre
) {
}
