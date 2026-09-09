package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ArestaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.CaminhoCriticoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.CaminhoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.DisciplinaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.GrafoResponse;
import br.edu.malhaia.adapters.in.web.security.SecurityUtils;
import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.application.usecase.BuscarMenorCaminhoUseCase;
import br.edu.malhaia.application.usecase.CalcularCaminhoCriticoUseCase;
import br.edu.malhaia.application.usecase.CarregarGrafoUseCase;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/grafo")
public class GrafoController {

	private final CarregarGrafoUseCase carregarGrafoUseCase;
	private final CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase;
	private final BuscarMenorCaminhoUseCase buscarMenorCaminhoUseCase;

	public GrafoController(
			CarregarGrafoUseCase carregarGrafoUseCase,
			CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase,
			BuscarMenorCaminhoUseCase buscarMenorCaminhoUseCase
	) {
		this.carregarGrafoUseCase = carregarGrafoUseCase;
		this.calcularCaminhoCriticoUseCase = calcularCaminhoCriticoUseCase;
		this.buscarMenorCaminhoUseCase = buscarMenorCaminhoUseCase;
	}

	@GetMapping
	public GrafoResponse grafo(@RequestParam(required = false) String semestre) {
		GrafoCurricular grafo = carregarGrafoUseCase.executar(semestre);
		List<DisciplinaResponse> disciplinas = grafo.getNos().values().stream()
				.map(GrafoController::toDisciplinaResponse)
				.toList();
		List<ArestaResponse> arestas = new ArrayList<>();
		for (var entry : grafo.getAdjacencia().entrySet()) {
			Long preReq = entry.getKey();
			for (Long disciplinaId : entry.getValue()) {
				arestas.add(new ArestaResponse(preReq, disciplinaId));
			}
		}
		return new GrafoResponse(disciplinas, arestas);
	}

	@GetMapping("/caminho-critico")
	public CaminhoCriticoResponse caminhoCritico(@RequestParam(required = false) String semestre) {
		CaminhoCriticoResultado resultado = calcularCaminhoCriticoUseCase.executar(semestre);
		return new CaminhoCriticoResponse(
				resultado.semestreMinimoPorDisciplina(),
				resultado.totalSemestres(),
				resultado.caminhoCriticoIds()
		);
	}

	@GetMapping("/caminho")
	public CaminhoResponse caminho(
			@RequestParam Long destino,
			@RequestParam(required = false) Long origem,
			@RequestParam(required = false) String semestre
	) {
		List<Long> caminho = buscarMenorCaminhoUseCase.executar(
				destino,
				Optional.ofNullable(origem),
				SecurityUtils.currentUserId(),
				semestre
		);
		return new CaminhoResponse(caminho);
	}

	private static DisciplinaResponse toDisciplinaResponse(Disciplina disciplina) {
		return new DisciplinaResponse(
				disciplina.id(),
				disciplina.nome(),
				disciplina.semestreSugerido(),
				disciplina.cargaHoraria()
		);
	}
}
