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
			@NotBlank(message = "Informe o e-mail")
			@Email(message = "E-mail inválido")
			@Size(max = 254, message = "E-mail muito longo")
			String email,
			@NotBlank(message = "Informe a senha")
			@Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
			String senha,
			@NotNull(message = "Informe o papel")
			Papel papel
	) {
	}

	public record LoginRequest(
			@NotBlank(message = "Informe o e-mail")
			@Email(message = "E-mail inválido")
			@Size(max = 254, message = "E-mail muito longo")
			String email,
			@NotBlank(message = "Informe a senha")
			@Size(min = 1, max = 100, message = "Senha inválida")
			String senha
	) {
	}

	public record AuthResponse(
			String token,
			java.util.UUID usuarioId,
			String email,
			Papel papel
	) {
	}

	public record AdminCriarUsuarioRequest(
			@NotBlank(message = "Informe o e-mail")
			@Email(message = "E-mail inválido")
			@Size(max = 254, message = "E-mail muito longo")
			String email,
			@NotBlank(message = "Informe a senha")
			@Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
			String senha,
			@NotNull(message = "Informe o papel")
			Papel papel
	) {
	}

	public record AdminUsuarioResponse(
			java.util.UUID usuarioId,
			String email,
			Papel papel
	) {
	}
}
