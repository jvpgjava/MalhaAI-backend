package br.edu.malhaia.application.graph;

import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DijkstraMenorCaminhoTest {

	private final DijkstraMenorCaminho dijkstra = new DijkstraMenorCaminho();

	/**
	 * Dois caminhos de A até D:
	 * A → B → C → D  (comprimento 3)
	 * A → E → D      (comprimento 2) ← menor
	 */
	@Test
	void escolheMenorCaminhoQuandoHaMaisDeUmaRota() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(2L, "B", 2, 60));
		grafo.adicionarDisciplina(new Disciplina(3L, "C", 3, 60));
		grafo.adicionarDisciplina(new Disciplina(4L, "D", 4, 60));
		grafo.adicionarDisciplina(new Disciplina(5L, "E", 2, 60));
		grafo.adicionarAresta(1L, 2L);
		grafo.adicionarAresta(2L, 3L);
		grafo.adicionarAresta(3L, 4L);
		grafo.adicionarAresta(1L, 5L);
		grafo.adicionarAresta(5L, 4L);

		List<Long> caminho = dijkstra.caminho(grafo, Set.of(1L), 4L);

		assertEquals(List.of(1L, 5L, 4L), caminho);
	}

	@Test
	void destinoJaNasOrigensRetornaSomenteDestino() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));

		List<Long> caminho = dijkstra.caminho(grafo, Set.of(1L), 1L);

		assertEquals(List.of(1L), caminho);
	}
}
