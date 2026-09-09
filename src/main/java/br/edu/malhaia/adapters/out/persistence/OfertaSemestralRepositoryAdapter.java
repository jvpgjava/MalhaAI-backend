package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.OfertaSemestralJpaEntity;
import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.OfertaSemestralJpaRepository;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.OfertaSemestral;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class OfertaSemestralRepositoryAdapter implements OfertaSemestralRepositoryPort {

	private final OfertaSemestralJpaRepository repository;

	public OfertaSemestralRepositoryAdapter(OfertaSemestralJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<OfertaSemestral> findAll() {
		return repository.findAll().stream().map(PersistenceMapper::toDomain).toList();
	}

	@Override
	public List<OfertaSemestral> findBySemestre(String semestre) {
		return repository.findBySemestre(semestre).stream().map(PersistenceMapper::toDomain).toList();
	}

	@Override
	public OfertaSemestral save(OfertaSemestral oferta) {
		OfertaSemestralJpaEntity entity = repository
				.findByDisciplinaIdAndSemestre(oferta.disciplinaId(), oferta.semestre())
				.orElseGet(OfertaSemestralJpaEntity::new);
		entity.setDisciplinaId(oferta.disciplinaId());
		entity.setSemestre(oferta.semestre());
		entity.setVagas(oferta.vagas());
		entity.setOfertada(oferta.ofertada());
		OfertaSemestralJpaEntity saved = repository.save(entity);
		return PersistenceMapper.toDomain(saved);
	}

	@Override
	public Set<Long> findDisciplinaIdsOfertadas(String semestre) {
		return repository.findDisciplinaIdsOfertadas(semestre);
	}
}
