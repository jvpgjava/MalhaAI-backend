package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.OfertaSemestralJpaEntity;
import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.OfertaSemestralJpaRepository;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.OfertaSemestral;
import br.edu.malhaia.domain.util.SemestreNormalizer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
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
		Set<String> variantes = SemestreNormalizer.variantes(semestre);
		if (variantes.isEmpty()) {
			return List.of();
		}
		return repository.findBySemestreIn(variantes).stream()
				.map(PersistenceMapper::toDomain)
				.toList();
	}

	@Override
	public OfertaSemestral save(OfertaSemestral oferta) {
		String semestreCanon = SemestreNormalizer.normalizar(oferta.semestre());
		OfertaSemestralJpaEntity entity = repository
				.findByDisciplinaIdAndSemestre(oferta.disciplinaId(), semestreCanon)
				.or(() -> SemestreNormalizer.variantes(oferta.semestre()).stream()
						.map(v -> repository.findByDisciplinaIdAndSemestre(oferta.disciplinaId(), v))
						.filter(java.util.Optional::isPresent)
						.map(java.util.Optional::get)
						.findFirst())
				.orElseGet(OfertaSemestralJpaEntity::new);
		entity.setDisciplinaId(oferta.disciplinaId());
		entity.setSemestre(semestreCanon);
		entity.setVagas(oferta.vagas());
		entity.setOfertada(oferta.ofertada());
		OfertaSemestralJpaEntity saved = repository.save(entity);
		return PersistenceMapper.toDomain(saved);
	}

	@Override
	public Set<Long> findDisciplinaIdsOfertadas(String semestre) {
		if (semestre == null || semestre.isBlank()) {
			return repository.findAll().stream()
					.filter(OfertaSemestralJpaEntity::isOfertada)
					.map(OfertaSemestralJpaEntity::getDisciplinaId)
					.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
		}
		Set<String> variantes = SemestreNormalizer.variantes(semestre);
		if (variantes.isEmpty()) {
			return Set.of();
		}
		return repository.findDisciplinaIdsOfertadasIn(variantes);
	}

	@Override
	public List<String> findSemestresDisponiveis() {
		return repository.findDistinctSemestres().stream()
				.map(SemestreNormalizer::normalizar)
				.filter(s -> s != null && !s.isBlank())
				.distinct()
				.sorted()
				.toList();
	}
}
