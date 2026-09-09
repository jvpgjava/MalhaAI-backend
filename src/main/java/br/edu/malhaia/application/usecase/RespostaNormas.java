package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.out.TrechoRecuperado;

import java.util.List;

public record RespostaNormas(
		String resposta,
		List<TrechoRecuperado> fontes
) {
}
