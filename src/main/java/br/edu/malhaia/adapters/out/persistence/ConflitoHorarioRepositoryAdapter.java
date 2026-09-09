package br.edu.malhaia.adapters.out.persistence;

import br.edu.malhaia.adapters.out.persistence.mapper.PersistenceMapper;
import br.edu.malhaia.adapters.out.persistence.repository.ConflitoHorarioJpaRepository;
import br.edu.malhaia.application.port.out.ConflitoHorarioRepositoryPort;
import br.edu.malhaia.domain.model.ConflitoHorario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConflitoHorarioRepositoryAdapter implements ConflitoHorarioRepositoryPort {

	private final ConflitoHorarioJpaRepository repository;

	public ConflitoHorarioRepositoryAdapter(ConflitoHorarioJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<ConflitoHorario> findAll() {
		return repository.findAll().stream().map(PersistenceMapper::toDomain).toList();
	}

	@Override
	public List<ConflitoHorario> findBySemestre(String semestre) {
		return repository.findBySemestre(semestre).stream().map(PersistenceMapper::toDomain).toList();
	}
}
