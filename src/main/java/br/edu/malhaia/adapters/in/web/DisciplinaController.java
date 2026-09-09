package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.DisciplinaRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.DisciplinaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.PreRequisitoRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.PreRequisitoResponse;
import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.application.port.out.PreRequisitoRepositoryPort;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.PreRequisito;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DisciplinaController {

	private final DisciplinaRepositoryPort disciplinaRepository;
	private final PreRequisitoRepositoryPort preRequisitoRepository;

	public DisciplinaController(
			DisciplinaRepositoryPort disciplinaRepository,
			PreRequisitoRepositoryPort preRequisitoRepository
	) {
		this.disciplinaRepository = disciplinaRepository;
		this.preRequisitoRepository = preRequisitoRepository;
	}

	@PostMapping("/disciplinas")
	@ResponseStatus(HttpStatus.CREATED)
	public DisciplinaResponse criar(@Valid @RequestBody DisciplinaRequest request) {
		Disciplina salva = disciplinaRepository.save(new Disciplina(
				null,
				request.nome(),
				request.semestreSugerido(),
				request.cargaHoraria()
		));
		return new DisciplinaResponse(salva.id(), salva.nome(), salva.semestreSugerido(), salva.cargaHoraria());
	}

	@PostMapping("/pre-requisitos")
	@ResponseStatus(HttpStatus.CREATED)
	public PreRequisitoResponse criarPreRequisito(@Valid @RequestBody PreRequisitoRequest request) {
		PreRequisito salvo = preRequisitoRepository.save(new PreRequisito(
				null,
				request.disciplinaId(),
				request.preRequisitoId()
		));
		return new PreRequisitoResponse(salvo.id(), salvo.disciplinaId(), salvo.preRequisitoId());
	}
}
