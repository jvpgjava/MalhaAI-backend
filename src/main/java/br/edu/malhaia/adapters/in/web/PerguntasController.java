package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.FonteResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.PerguntaRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.PerguntaResponse;
import br.edu.malhaia.application.usecase.ConsultarNormasUseCase;
import br.edu.malhaia.application.usecase.RespostaNormas;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perguntas")
public class PerguntasController {

	private final ConsultarNormasUseCase consultarNormasUseCase;

	public PerguntasController(ConsultarNormasUseCase consultarNormasUseCase) {
		this.consultarNormasUseCase = consultarNormasUseCase;
	}

	@PostMapping
	public PerguntaResponse perguntar(@Valid @RequestBody PerguntaRequest request) {
		RespostaNormas resposta = consultarNormasUseCase.executar(request.pergunta());
		return new PerguntaResponse(
				resposta.resposta(),
				resposta.fontes().stream()
						.map(f -> new FonteResponse(f.documentoId(), f.titulo(), f.trecho(), f.similaridade()))
						.toList()
		);
	}
}
