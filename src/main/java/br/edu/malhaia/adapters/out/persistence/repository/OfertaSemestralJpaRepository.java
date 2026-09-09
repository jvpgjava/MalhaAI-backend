package br.edu.malhaia.adapters.out.persistence.repository;

import br.edu.malhaia.adapters.out.persistence.entity.OfertaSemestralJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OfertaSemestralJpaRepository extends JpaRepository<OfertaSemestralJpaEntity, Long> {

	List<OfertaSemestralJpaEntity> findBySemestre(String semestre);

	Optional<OfertaSemestralJpaEntity> findByDisciplinaIdAndSemestre(Long disciplinaId, String semestre);

	@Query("""
			select o.disciplinaId from OfertaSemestralJpaEntity o
			where o.ofertada = true
			  and (:semestre is null or o.semestre = :semestre)
			""")
	Set<Long> findDisciplinaIdsOfertadas(@Param("semestre") String semestre);
}
