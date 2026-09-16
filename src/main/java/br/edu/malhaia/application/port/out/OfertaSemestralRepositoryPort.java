package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.OfertaSemestral;

import java.util.List;
import java.util.Set;

public interface OfertaSemestralRepositoryPort {

	List<OfertaSemestral> findAll();

	List<OfertaSemestral> findBySemestre(String semestre);

	OfertaSemestral save(OfertaSemestral oferta);

	/**
	 * Ids das disciplinas com {@code ofertada=true} no semestre (aceita 2025.1 / 2025/1).
	 * Se {@code semestre} for null, considera todas as ofertas com ofertada=true.
	 */
	Set<Long> findDisciplinaIdsOfertadas(String semestre);

	List<String> findSemestresDisponiveis();
}
