package br.edu.malhaia.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrafoCurricularRemoverConcluidasTest {

	@Test
	void removeConcluidasELiberaDependentesComoFontes() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(2L, "B", 2, 60));
		grafo.adicionarDisciplina(new Disciplina(3L, "C", 3, 60));
		grafo.adicionarAresta(1L, 2L);
		grafo.adicionarAresta(2L, 3L);

		GrafoCurricular restante = grafo.removerConcluidas(Set.of(1L));

		assertFalse(restante.getNos().containsKey(1L));
		assertTrue(restante.getNos().containsKey(2L));
		assertTrue(restante.getNos().containsKey(3L));
		assertTrue(restante.getPreRequisitos().get(2L).isEmpty());
		assertEquals(Set.of(2L), restante.getPreRequisitos().get(3L));
	}
}
