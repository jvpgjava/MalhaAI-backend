package br.edu.malhaia.application.usecase;

public record ElegibilidadeRea(
		Long disciplinaId,
		String motivo
) {
	public static final String MOTIVO_NAO_OFERTADA = "NAO_OFERTADA";
	public static final String MOTIVO_CONFLITO_HORARIO = "CONFLITO_HORARIO";
}
