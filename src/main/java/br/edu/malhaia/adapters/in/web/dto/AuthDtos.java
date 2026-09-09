package br.edu.malhaia.adapters.in.web.dto;

import br.edu.malhaia.domain.model.Papel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

	private AuthDtos() {
	}

	public record CadastroRequest(
			@NotBlank @Email String email,
			@NotBlank @Size(min = 6, max = 100) String senha,
			@NotNull Papel papel
	) {
	}

	public record LoginRequest(
			@NotBlank @Email String email,
			@NotBlank String senha
	) {
	}

	public record AuthResponse(
			String token,
			java.util.UUID usuarioId,
			String email,
			Papel papel
	) {
	}
}
