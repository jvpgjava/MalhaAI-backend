package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.DisciplinaJpaEntity;
import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.DisciplinaJpaRepository;
import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.domain.model.Disciplina;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DisciplinaRepositoryAdapter implements DisciplinaRepositoryPort {

	private final DisciplinaJpaRepository repository;

	public DisciplinaRepositoryAdapter(DisciplinaJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Disciplina> findAll() {
		return repository.findAll().stream().map(PersistenceMapper::toDomain).toList();
	}

	@Override
	public Optional<Disciplina> findById(Long id) {
		return repository.findById(id).map(PersistenceMapper::toDomain);
	}

	@Override
	public Disciplina save(Disciplina disciplina) {
		DisciplinaJpaEntity saved = repository.save(PersistenceMapper.toEntity(disciplina));
		return PersistenceMapper.toDomain(saved);
	}
}
