package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.graph.CaminhoCriticoPort;
import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.domain.model.AlunoProgresso;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class CalcularCaminhoCriticoUseCase {

	private final CarregarGrafoUseCase carregarGrafoUseCase;
	private final CaminhoCriticoPort caminhoCriticoPort;
	private final AlunoProgressoRepositoryPort alunoProgressoRepository;

	public CalcularCaminhoCriticoUseCase(
			CarregarGrafoUseCase carregarGrafoUseCase,
			CaminhoCriticoPort caminhoCriticoPort,
			AlunoProgressoRepositoryPort alunoProgressoRepository
	) {
		this.carregarGrafoUseCase = carregarGrafoUseCase;
		this.caminhoCriticoPort = caminhoCriticoPort;
		this.alunoProgressoRepository = alunoProgressoRepository;
	}

	/**
	 * @param semestreOferta se informado, restringe às ofertadas do semestre
	 * @param usuarioId se informado, remove concluídas e calcula o caminho restante
	 */
	public CaminhoCriticoResultado executar(String semestreOferta, UUID usuarioId) {
		GrafoCurricular grafo = carregarGrafoUseCase.executar(semestreOferta);
		if (usuarioId != null) {
			Set<Long> concluidas = alunoProgressoRepository.findByUsuarioId(usuarioId)
					.map(AlunoProgresso::disciplinasConcluidas)
					.orElse(Set.of());
			if (concluidas != null && !concluidas.isEmpty()) {
				grafo = grafo.removerConcluidas(concluidas);
			}
		}
		return caminhoCriticoPort.calcular(grafo);
	}

	public CaminhoCriticoResultado executar(String semestreOferta) {
		return executar(semestreOferta, null);
	}

	public CaminhoCriticoResultado executar() {
		return executar(null, null);
	}
}
