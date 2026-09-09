package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.application.port.out.LlmPort;
import br.edu.malhaia.domain.exception.DisciplinaNaoEncontradaException;
import br.edu.malhaia.domain.exception.LlmIndisponivelException;
import br.edu.malhaia.domain.model.Disciplina;
import org.springframework.stereotype.Service;

@Service
public class GerarExplicacaoUseCase {

	private static final String SYSTEM_PROMPT = """
			Você é um assistente acadêmico que explica resultados determinísticos do grafo curricular.
			Use apenas os fatos fornecidos. Não invente pré-requisitos, semestres ou regras.
			Responda em português de forma clara e objetiva.
			""";

	private final CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase;
	private final DisciplinaRepositoryPort disciplinaRepository;
	private final LlmPort llmPort;

	public GerarExplicacaoUseCase(
			CalcularCaminhoCriticoUseCase calcularCaminhoCriticoUseCase,
			DisciplinaRepositoryPort disciplinaRepository,
			LlmPort llmPort
	) {
		this.calcularCaminhoCriticoUseCase = calcularCaminhoCriticoUseCase;
		this.disciplinaRepository = disciplinaRepository;
		this.llmPort = llmPort;
	}

	public String executar(Long disciplinaId, String semestreOferta) {
		Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
				.orElseThrow(() -> new DisciplinaNaoEncontradaException(disciplinaId));

		CaminhoCriticoResultado critico = calcularCaminhoCriticoUseCase.executar(semestreOferta);
		Integer semestreMin = critico.semestreMinimoPorDisciplina().get(disciplinaId);
		boolean noCaminhoCritico = critico.caminhoCriticoIds().contains(disciplinaId);

		String userPrompt = """
				Fatos do grafo curricular:
				- Disciplina: id=%d, nome=%s, semestreSugerido=%d, cargaHoraria=%d
				- Semestre mínimo calculado para esta disciplina: %s
				- Total de semestres do caminho crítico do currículo: %d
				- Esta disciplina está no caminho crítico? %s
				- Ids do caminho crítico: %s

				Explique o papel desta disciplina no percurso até a formatura e o impacto
				se ela for atrasada ou reprovada, com base apenas nesses fatos.
				""".formatted(
				disciplina.id(),
				disciplina.nome(),
				disciplina.semestreSugerido(),
				disciplina.cargaHoraria(),
				semestreMin == null ? "não disponível (fora do subgrafo)" : semestreMin,
				critico.totalSemestres(),
				noCaminhoCritico ? "sim" : "não",
				critico.caminhoCriticoIds()
		);

		try {
			return llmPort.gerarTexto(SYSTEM_PROMPT, userPrompt);
		} catch (LlmIndisponivelException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new LlmIndisponivelException("Serviço de LLM indisponível", e);
		}
	}

	public String executar(Long disciplinaId) {
		return executar(disciplinaId, null);
	}
}
