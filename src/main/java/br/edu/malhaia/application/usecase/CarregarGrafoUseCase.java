package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.application.port.out.PreRequisitoRepositoryPort;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import br.edu.malhaia.domain.model.PreRequisito;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CarregarGrafoUseCase {

	private final DisciplinaRepositoryPort disciplinaRepository;
	private final PreRequisitoRepositoryPort preRequisitoRepository;
	private final OfertaSemestralRepositoryPort ofertaRepository;

	public CarregarGrafoUseCase(
			DisciplinaRepositoryPort disciplinaRepository,
			PreRequisitoRepositoryPort preRequisitoRepository,
			OfertaSemestralRepositoryPort ofertaRepository
	) {
		this.disciplinaRepository = disciplinaRepository;
		this.preRequisitoRepository = preRequisitoRepository;
		this.ofertaRepository = ofertaRepository;
	}

	/**
	 * Monta o grafo curricular. Se {@code semestreOferta} for informado, filtra
	 * apenas disciplinas ofertadas naquele semestre (ou todas ofertadas se o
	 * repositório tratar null internamente — aqui passa o valor recebido).
	 */
	public GrafoCurricular executar(String semestreOferta) {
		GrafoCurricular grafo = new GrafoCurricular();
		List<Disciplina> disciplinas = disciplinaRepository.findAll();
		for (Disciplina disciplina : disciplinas) {
			grafo.adicionarDisciplina(disciplina);
		}
		for (PreRequisito aresta : preRequisitoRepository.findAll()) {
			if (grafo.getNos().containsKey(aresta.preRequisitoId())
					&& grafo.getNos().containsKey(aresta.disciplinaId())) {
				grafo.adicionarAresta(aresta.preRequisitoId(), aresta.disciplinaId());
			}
		}

		if (semestreOferta != null) {
			Set<Long> ofertadas = ofertaRepository.findDisciplinaIdsOfertadas(semestreOferta);
			return grafo.filtrarPorOfertadas(ofertadas);
		}
		return grafo;
	}

	public GrafoCurricular executar() {
		return executar(null);
	}
}
