package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.graph.MenorCaminhoPort;
import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.domain.exception.DisciplinaNaoEncontradaException;
import br.edu.malhaia.domain.model.AlunoProgresso;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BuscarMenorCaminhoUseCase {

	private final CarregarGrafoUseCase carregarGrafoUseCase;
	private final MenorCaminhoPort menorCaminhoPort;
	private final AlunoProgressoRepositoryPort alunoProgressoRepository;

	public BuscarMenorCaminhoUseCase(
			CarregarGrafoUseCase carregarGrafoUseCase,
			MenorCaminhoPort menorCaminhoPort,
			AlunoProgressoRepositoryPort alunoProgressoRepository
	) {
		this.carregarGrafoUseCase = carregarGrafoUseCase;
		this.menorCaminhoPort = menorCaminhoPort;
		this.alunoProgressoRepository = alunoProgressoRepository;
	}

	public List<Long> executar(Long destino, Optional<Long> origem, UUID usuarioId, String semestreOferta) {
		GrafoCurricular grafo = carregarGrafoUseCase.executar(semestreOferta);
		if (!grafo.getNos().containsKey(destino)) {
			throw new DisciplinaNaoEncontradaException(destino);
		}

		Set<Long> fontes;
		boolean origemExplicita = origem.isPresent();

		if (origemExplicita) {
			Long origemId = origem.get();
			if (!grafo.getNos().containsKey(origemId)) {
				throw new DisciplinaNaoEncontradaException(origemId);
			}
			fontes = Set.of(origemId);
		} else {
			Set<Long> concluidas = alunoProgressoRepository.findByUsuarioId(usuarioId)
					.map(AlunoProgresso::disciplinasConcluidas)
					.orElse(Set.of());
			if (concluidas == null || concluidas.isEmpty()) {
				fontes = grafo.getNos().keySet().stream()
						.filter(id -> grafo.getPreRequisitos().getOrDefault(id, Set.of()).isEmpty())
						.collect(Collectors.toCollection(HashSet::new));
			} else {
				fontes = new HashSet<>(concluidas);
			}
		}

		List<Long> caminho = menorCaminhoPort.caminho(grafo, fontes, destino);

		// Não inclui disciplinas já concluídas no caminho, exceto origem explícita
		// ou quando o destino já está nas fontes (lista só com destino).
		if (!origemExplicita && caminho.size() > 1) {
			Set<Long> concluidas = alunoProgressoRepository.findByUsuarioId(usuarioId)
					.map(AlunoProgresso::disciplinasConcluidas)
					.orElse(Set.of());
			if (concluidas != null && !concluidas.isEmpty()) {
				caminho = caminho.stream()
						.filter(id -> !concluidas.contains(id) || id.equals(destino))
						.toList();
			}
		}

		return caminho;
	}

	public List<Long> executar(Long destino, Optional<Long> origem, UUID usuarioId) {
		return executar(destino, origem, usuarioId, null);
	}
}
