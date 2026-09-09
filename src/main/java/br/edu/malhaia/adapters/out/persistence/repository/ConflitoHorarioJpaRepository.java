package br.edu.malhaia.adapters.out.persistence.repository;

import br.edu.malhaia.adapters.out.persistence.entity.ConflitoHorarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConflitoHorarioJpaRepository extends JpaRepository<ConflitoHorarioJpaEntity, Long> {

	List<ConflitoHorarioJpaEntity> findBySemestre(String semestre);
}
