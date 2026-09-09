package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ExplicacaoResponse;
import br.edu.malhaia.application.usecase.GerarExplicacaoUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/explicacao")
public class ExplicacaoController {

	private final GerarExplicacaoUseCase gerarExplicacaoUseCase;

	public ExplicacaoController(GerarExplicacaoUseCase gerarExplicacaoUseCase) {
		this.gerarExplicacaoUseCase = gerarExplicacaoUseCase;
	}

	@GetMapping("/{disciplinaId}")
	public ExplicacaoResponse explicar(
			@PathVariable Long disciplinaId,
			@RequestParam(required = false) String semestre
	) {
		return new ExplicacaoResponse(gerarExplicacaoUseCase.executar(disciplinaId, semestre));
	}
}
