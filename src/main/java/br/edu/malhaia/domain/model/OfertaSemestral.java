package br.edu.malhaia.domain.model;

public record OfertaSemestral(
		Long id,
		Long disciplinaId,
		String semestre,
		int vagas,
		boolean ofertada
) {
}
