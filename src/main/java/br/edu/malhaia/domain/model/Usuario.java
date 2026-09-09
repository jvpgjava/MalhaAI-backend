package br.edu.malhaia.domain.model;

import java.util.UUID;

public record Usuario(
		UUID id,
		String email,
		String senhaHash,
		Papel papel
) {
}
