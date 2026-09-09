package br.edu.malhaia.domain.model;

public record Disciplina(
		Long id,
		String nome,
		int semestreSugerido,
		int cargaHoraria
) {
}
