package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.AuthDtos.AdminCriarUsuarioRequest;
import br.edu.malhaia.adapters.in.web.dto.AuthDtos.AdminUsuarioResponse;
import br.edu.malhaia.application.port.out.UsuarioRepositoryPort;
import br.edu.malhaia.domain.exception.EmailJaCadastradoException;
import br.edu.malhaia.domain.model.Usuario;
import br.edu.malhaia.domain.util.TextoSeguro;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('DEV')")
public class AdminController {

	private final UsuarioRepositoryPort usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminController(UsuarioRepositoryPort usuarioRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/usuarios")
	@ResponseStatus(HttpStatus.CREATED)
	public AdminUsuarioResponse criarUsuario(@Valid @RequestBody AdminCriarUsuarioRequest request) {
		String email = TextoSeguro.normalizarEmail(request.email());
		if (usuarioRepository.findByEmail(email).isPresent()) {
			throw new EmailJaCadastradoException();
		}
		Usuario usuario = new Usuario(
				UUID.randomUUID(),
				email,
				passwordEncoder.encode(request.senha()),
				request.papel()
		);
		Usuario salvo = usuarioRepository.save(usuario);
		return new AdminUsuarioResponse(salvo.id(), salvo.email(), salvo.papel());
	}
}
