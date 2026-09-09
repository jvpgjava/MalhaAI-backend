package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.ConflitoHorario;

import java.util.List;

public interface ConflitoHorarioRepositoryPort {

	List<ConflitoHorario> findAll();

	List<ConflitoHorario> findBySemestre(String semestre);
}
