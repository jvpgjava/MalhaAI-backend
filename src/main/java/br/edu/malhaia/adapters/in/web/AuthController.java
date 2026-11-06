package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.AuthDtos.AuthResponse;
import br.edu.malhaia.adapters.in.web.dto.AuthDtos.CadastroRequest;
import br.edu.malhaia.adapters.in.web.dto.AuthDtos.LoginRequest;
import br.edu.malhaia.adapters.in.web.security.JwtService;
import br.edu.malhaia.application.port.out.UsuarioRepositoryPort;
import br.edu.malhaia.domain.exception.AcessoNegadoException;
import br.edu.malhaia.domain.exception.EmailJaCadastradoException;
import br.edu.malhaia.domain.model.Papel;
import br.edu.malhaia.domain.model.Usuario;
import br.edu.malhaia.domain.util.TextoSeguro;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioRepositoryPort usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final String dummyHash;

	public AuthController(
			UsuarioRepositoryPort usuarioRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService
	) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.dummyHash = passwordEncoder.encode("timing-pad-not-a-real-account");
	}

	@PostMapping("/cadastro")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthResponse cadastro(@Valid @RequestBody CadastroRequest request) {
		if (request.papel() != Papel.ALUNO) {
			throw new AcessoNegadoException("Acesso negado");
		}
		String email = TextoSeguro.normalizarEmail(request.email());
		if (usuarioRepository.findByEmail(email).isPresent()) {
			throw new EmailJaCadastradoException();
		}
		Usuario usuario = new Usuario(
				UUID.randomUUID(),
				email,
				passwordEncoder.encode(request.senha()),
				Papel.ALUNO
		);
		Usuario salvo = usuarioRepository.save(usuario);
		String token = jwtService.gerarToken(salvo.id(), salvo.email(), salvo.papel());
		return new AuthResponse(token, salvo.id(), salvo.email(), salvo.papel());
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		String email = TextoSeguro.normalizarEmail(request.email());
		Optional<Usuario> encontrado = usuarioRepository.findByEmail(email);
		String hash = encontrado.map(Usuario::senhaHash).orElse(dummyHash);
		boolean senhaOk = passwordEncoder.matches(request.senha(), hash);
		if (encontrado.isEmpty() || !senhaOk) {
			throw new BadCredentialsException("Credenciais inválidas");
		}
		Usuario usuario = encontrado.get();
		String token = jwtService.gerarToken(usuario.id(), usuario.email(), usuario.papel());
		return new AuthResponse(token, usuario.id(), usuario.email(), usuario.papel());
	}
}
