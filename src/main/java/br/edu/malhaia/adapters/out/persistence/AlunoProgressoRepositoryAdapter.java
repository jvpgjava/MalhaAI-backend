package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.entity.AlunoProgressoDisciplinaJpaEntity;
import br.edu.malhaia.adapters.out.persistence.repository.AlunoProgressoJpaRepository;
import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.domain.model.AlunoProgresso;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AlunoProgressoRepositoryAdapter implements AlunoProgressoRepositoryPort {

	private final AlunoProgressoJpaRepository repository;

	public AlunoProgressoRepositoryAdapter(AlunoProgressoJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<AlunoProgresso> findByUsuarioId(UUID usuarioId) {
		Set<Long> disciplinas = repository.findByUsuarioId(usuarioId).stream()
				.map(AlunoProgressoDisciplinaJpaEntity::getDisciplinaId)
				.collect(Collectors.toCollection(HashSet::new));
		if (disciplinas.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new AlunoProgresso(usuarioId, disciplinas));
	}

	@Override
	@Transactional
	public AlunoProgresso save(AlunoProgresso progresso) {
		repository.deleteByUsuarioId(progresso.usuarioId());
		Set<Long> disciplinas = progresso.disciplinasConcluidas() == null
				? Set.of()
				: progresso.disciplinasConcluidas();
		for (Long disciplinaId : disciplinas) {
			repository.save(new AlunoProgressoDisciplinaJpaEntity(progresso.usuarioId(), disciplinaId));
		}
		return new AlunoProgresso(progresso.usuarioId(), new HashSet<>(disciplinas));
	}
}
