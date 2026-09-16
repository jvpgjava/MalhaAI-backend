package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OfertaRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OfertaResponse;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.OfertaSemestral;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/oferta")
public class OfertaController {

	private final OfertaSemestralRepositoryPort ofertaRepository;

	public OfertaController(OfertaSemestralRepositoryPort ofertaRepository) {
		this.ofertaRepository = ofertaRepository;
	}

	@GetMapping
	public List<OfertaResponse> listar(@RequestParam String semestre) {
		return ofertaRepository.findBySemestre(semestre).stream()
				.map(OfertaController::toResponse)
				.toList();
	}

	@GetMapping("/semestres")
	public List<String> semestres() {
		List<String> db = ofertaRepository.findSemestresDisponiveis();
		if (!db.isEmpty()) {
			return db;
		}
		return br.edu.malhaia.domain.util.SemestreNormalizer.presets();
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
		return toResponse(salva);
	}

	private static OfertaResponse toResponse(OfertaSemestral oferta) {
		return new OfertaResponse(
				oferta.id(),
				oferta.disciplinaId(),
				oferta.semestre(),
				oferta.vagas(),
				oferta.ofertada()
		);
	}
}
