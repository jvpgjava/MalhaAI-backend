package br.edu.malhaia.adapters.in.web.security;

import br.edu.malhaia.application.port.out.UsuarioRepositoryPort;
import br.edu.malhaia.domain.model.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepositoryPort usuarioRepository;

	public UsuarioDetailsService(UsuarioRepositoryPort usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
		return new UsuarioPrincipal(usuario);
	}
}
