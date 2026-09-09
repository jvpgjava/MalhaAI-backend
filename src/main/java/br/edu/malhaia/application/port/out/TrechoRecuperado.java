package br.edu.malhaia.application.port.out;

public record TrechoRecuperado(
		Long documentoId,
		String titulo,
		String trecho,
		double similaridade
) {
}
