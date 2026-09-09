package br.edu.malhaia.adapters.out.persistence.repository;

import br.edu.malhaia.adapters.out.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, UUID> {

	Optional<UsuarioJpaEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}
