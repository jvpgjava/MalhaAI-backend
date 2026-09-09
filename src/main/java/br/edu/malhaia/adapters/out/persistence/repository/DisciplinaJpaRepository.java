package br.edu.malhaia.adapters.out.persistence.repository;

import br.edu.malhaia.adapters.out.persistence.entity.DisciplinaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplinaJpaRepository extends JpaRepository<DisciplinaJpaEntity, Long> {
}
