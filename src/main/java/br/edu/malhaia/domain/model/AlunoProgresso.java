package br.edu.malhaia.domain.model;

import java.util.Set;
import java.util.UUID;

public record AlunoProgresso(
		UUID usuarioId,
		Set<Long> disciplinasConcluidas
) {
}
