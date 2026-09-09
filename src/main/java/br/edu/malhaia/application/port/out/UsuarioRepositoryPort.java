package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositoryPort {

	Optional<Usuario> findById(UUID id);

	Optional<Usuario> findByEmail(String email);

	Usuario save(Usuario usuario);
}
