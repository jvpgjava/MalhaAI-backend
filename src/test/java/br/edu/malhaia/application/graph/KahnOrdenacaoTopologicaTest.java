package br.edu.malhaia.application.graph;

import br.edu.malhaia.domain.exception.GrafoCiclicoException;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KahnOrdenacaoTopologicaTest {

	private final KahnOrdenacaoTopologica kahn = new KahnOrdenacaoTopologica();

	@Test
	void grafoSemCicloOrdenaCorretamente() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(2L, "B", 2, 60));
		grafo.adicionarDisciplina(new Disciplina(3L, "C", 3, 60));
		grafo.adicionarAresta(1L, 2L);
		grafo.adicionarAresta(2L, 3L);

		List<Long> ordem = kahn.ordenar(grafo);

		assertEquals(3, ordem.size());
		assertTrue(ordem.indexOf(1L) < ordem.indexOf(2L));
		assertTrue(ordem.indexOf(2L) < ordem.indexOf(3L));
	}

	@Test
	void grafoComCicloLancaGrafoCiclicoException() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(2L, "B", 2, 60));
		grafo.adicionarDisciplina(new Disciplina(3L, "C", 3, 60));
		grafo.adicionarAresta(1L, 2L);
		grafo.adicionarAresta(2L, 3L);
		grafo.adicionarAresta(3L, 1L);

		assertThrows(GrafoCiclicoException.class, () -> kahn.ordenar(grafo));
	}
}
