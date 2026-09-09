package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OfertaRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OfertaResponse;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.OfertaSemestral;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oferta")
public class OfertaController {

	private final OfertaSemestralRepositoryPort ofertaRepository;

	public OfertaController(OfertaSemestralRepositoryPort ofertaRepository) {
		this.ofertaRepository = ofertaRepository;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('COORDENACAO')")
	public OfertaResponse criar(@Valid @RequestBody OfertaRequest request) {
		OfertaSemestral salva = ofertaRepository.save(new OfertaSemestral(
				null,
				request.disciplinaId(),
				request.semestre(),
				request.vagas(),
				request.ofertada()
		));
		return new OfertaResponse(
				salva.id(),
				salva.disciplinaId(),
				salva.semestre(),
				salva.vagas(),
				salva.ofertada()
		);
	}
}
