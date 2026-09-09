package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.AuthDtos.AuthResponse;
import br.edu.malhaia.adapters.in.web.dto.AuthDtos.CadastroRequest;
import br.edu.malhaia.adapters.in.web.dto.AuthDtos.LoginRequest;
import br.edu.malhaia.adapters.in.web.security.JwtService;
import br.edu.malhaia.application.port.out.UsuarioRepositoryPort;
import br.edu.malhaia.domain.exception.EmailJaCadastradoException;
import br.edu.malhaia.domain.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioRepositoryPort usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthController(
			UsuarioRepositoryPort usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService
	) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@PostMapping("/cadastro")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse cadastro(@Valid @RequestBody CadastroRequest request) {
		if (usuarioRepository.findByEmail(request.email()).isPresent()) {
			throw new EmailJaCadastradoException(request.email());
		}
		Usuario usuario = new Usuario(
				UUID.randomUUID(),
				request.email().trim().toLowerCase(),
				passwordEncoder.encode(request.senha()),
				request.papel()
		);
		Usuario salvo = usuarioRepository.save(usuario);
		String token = jwtService.gerarToken(salvo.id(), salvo.email(), salvo.papel());
		return new AuthResponse(token, salvo.id(), salvo.email(), salvo.papel());
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(request.email().trim().toLowerCase())
				.orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));
		if (!passwordEncoder.matches(request.senha(), usuario.senhaHash())) {
			throw new BadCredentialsException("Credenciais inválidas");
		}
		String token = jwtService.gerarToken(usuario.id(), usuario.email(), usuario.papel());
		return new AuthResponse(token, usuario.id(), usuario.email(), usuario.papel());
	}
}
