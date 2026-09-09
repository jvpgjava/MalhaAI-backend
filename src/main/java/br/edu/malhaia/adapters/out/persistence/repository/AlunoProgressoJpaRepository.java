package br.edu.malhaia.adapters.out.persistence.repository;

import br.edu.malhaia.adapters.out.persistence.entity.AlunoProgressoDisciplinaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlunoProgressoJpaRepository
		extends JpaRepository<AlunoProgressoDisciplinaJpaEntity, AlunoProgressoDisciplinaJpaEntity.Pk> {

	List<AlunoProgressoDisciplinaJpaEntity> findByUsuarioId(UUID usuarioId);

	@Modifying(clearAutomatically = true)
	@Query("delete from AlunoProgressoDisciplinaJpaEntity a where a.usuarioId = :usuarioId")
	void deleteByUsuarioId(@Param("usuarioId") UUID usuarioId);
}
