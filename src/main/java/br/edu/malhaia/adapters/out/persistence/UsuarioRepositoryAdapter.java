package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.UsuarioJpaRepository;
import br.edu.malhaia.application.port.out.UsuarioRepositoryPort;
import br.edu.malhaia.domain.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

	private final UsuarioJpaRepository repository;

	public UsuarioRepositoryAdapter(UsuarioJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Usuario> findById(UUID id) {
		return repository.findById(id).map(PersistenceMapper::toDomain);
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		return repository.findByEmail(email).map(PersistenceMapper::toDomain);
	}

	@Override
	public Usuario save(Usuario usuario) {
		UsuarioJpaEntity saved = repository.save(PersistenceMapper.toEntity(usuario));
		return PersistenceMapper.toDomain(saved);
	}
}
