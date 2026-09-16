package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ArestaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.CaminhoCriticoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.CaminhoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.DisciplinaOrientacaoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.DisciplinaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.GrafoResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OrientacaoEstruturadaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OrientacaoRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.OrientacaoResponse;
import br.edu.malhaia.adapters.in.web.security.SecurityUtils;
import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.application.usecase.BuscarMenorCaminhoUseCase;
import br.edu.malhaia.application.usecase.CalcularCaminhoCriticoUseCase;
import br.edu.malhaia.application.usecase.CarregarGrafoUseCase;
import br.edu.malhaia.application.usecase.OrientarPercursoUseCase;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/grafo")
public class GrafoController {

	private final CarregarGrafoUseCase carregarGrafoUseCase;
	private final CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase;
	private final BuscarMenorCaminhoUseCase buscarMenorCaminhoUseCase;
	private final OrientarPercursoUseCase orientarPercursoUseCase;

	public GrafoController(
			CarregarGrafoUseCase carregarGrafoUseCase,
			CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase,
			BuscarMenorCaminhoUseCase buscarMenorCaminhoUseCase,
			OrientarPercursoUseCase orientarPercursoUseCase
	) {
		this.carregarGrafoUseCase = carregarGrafoUseCase;
		this.calcularCaminhoCriticoUseCase = calcularCaminhoCriticoUseCase;
		this.buscarMenorCaminhoUseCase = buscarMenorCaminhoUseCase;
		this.orientarPercursoUseCase = orientarPercursoUseCase;
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
	public CaminhoCriticoResponse caminhoCritico(
			@RequestParam(required = false) String semestre,
			@RequestParam(defaultValue = "true") boolean considerarProgresso
	) {
		UUID usuarioId = considerarProgresso ? SecurityUtils.currentUserId() : null;
		CaminhoCriticoResultado resultado = calcularCaminhoCriticoUseCase.executar(semestre, usuarioId);
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

	@PostMapping("/orientacao")
	public OrientacaoResponse orientar(@Valid @RequestBody OrientacaoRequest request) {
		var resultado = orientarPercursoUseCase.executar(
				SecurityUtils.currentUserId(),
				request.semestre(),
				Optional.ofNullable(request.destinoPrioridadeId())
		);
		var o = resultado.orientacao();
		return new OrientacaoResponse(
				resultado.semestre(),
				resultado.modo(),
				resultado.destinoId(),
				resultado.caminhoIds(),
				resultado.caminhoNomes(),
				resultado.proximasOfertadasIds(),
				resultado.proximasOfertadasNomes(),
				resultado.primeiraDisciplinaId(),
				resultado.primeiraDisciplinaNome(),
				new OrientacaoEstruturadaResponse(
						o.resumo(),
						o.ordemSugerida(),
						o.disciplinas().stream()
								.map(d -> new DisciplinaOrientacaoResponse(
										d.nome(), d.porqueNessaOrdem(), d.sobre()
								))
								.toList(),
						o.proximosPassos(),
						o.alertas(),
						o.estruturado()
				),
				resultado.iaDisponivel(),
				resultado.roadmapIndexado(),
				List.of() // fontes internas (RAG/web) — não exibidas ao aluno
		);
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
