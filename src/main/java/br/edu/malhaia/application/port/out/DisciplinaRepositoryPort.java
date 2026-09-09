package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.Disciplina;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepositoryPort {

	List<Disciplina> findAll();

	Optional<Disciplina> findById(Long id);

	Disciplina save(Disciplina disciplina);
}
