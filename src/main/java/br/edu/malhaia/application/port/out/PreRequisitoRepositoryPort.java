package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.PreRequisito;

import java.util.List;

public interface PreRequisitoRepositoryPort {

	List<PreRequisito> findAll();

	PreRequisito save(PreRequisito preRequisito);
}
