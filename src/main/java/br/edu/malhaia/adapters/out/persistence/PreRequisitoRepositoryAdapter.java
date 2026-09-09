package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.PreRequisitoJpaEntity;
import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.PreRequisitoJpaRepository;
import br.edu.malhaia.application.port.out.PreRequisitoRepositoryPort;
import br.edu.malhaia.domain.model.PreRequisito;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreRequisitoRepositoryAdapter implements PreRequisitoRepositoryPort {

	private final PreRequisitoJpaRepository repository;

	public PreRequisitoRepositoryAdapter(PreRequisitoJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<PreRequisito> findAll() {
		return repository.findAll().stream().map(PersistenceMapper::toDomain).toList();
	}

	@Override
	public PreRequisito save(PreRequisito preRequisito) {
		PreRequisitoJpaEntity saved = repository.save(PersistenceMapper.toEntity(preRequisito));
		return PersistenceMapper.toDomain(saved);
	}
}
