package br.edu.malhaia.application.graph;

import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CaminhoCriticoCalculatorTest {

	private final CaminhoCriticoCalculator calculator = new CaminhoCriticoCalculator();

	/**
	 * Convergência: D depende de B (nível 2) e C (nível 3 via A→C).
	 * A(1) → B(2)
	 * A(1) → C(2) → D? Wait, better:
	 * A → B → D
	 * C → D
	 * So B is at semestre 2, C at 1, D = 1 + max(2,1) = 3
	 */
	@Test
	void convergenciaComPreRequisitosEmNiveisDiferentes() {
		GrafoCurricular grafo = new GrafoCurricular();
		grafo.adicionarDisciplina(new Disciplina(1L, "A", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(2L, "B", 2, 60));
		grafo.adicionarDisciplina(new Disciplina(3L, "C", 1, 60));
		grafo.adicionarDisciplina(new Disciplina(4L, "D", 3, 60));
		grafo.adicionarAresta(1L, 2L); // A → B
		grafo.adicionarAresta(2L, 4L); // B → D
		grafo.adicionarAresta(3L, 4L); // C → D

		CaminhoCriticoResultado resultado = calculator.calcular(grafo);

		assertEquals(1, resultado.semestreMinimoPorDisciplina().get(1L));
		assertEquals(2, resultado.semestreMinimoPorDisciplina().get(2L));
		assertEquals(1, resultado.semestreMinimoPorDisciplina().get(3L));
		assertEquals(3, resultado.semestreMinimoPorDisciplina().get(4L));
		assertEquals(3, resultado.totalSemestres());
		assertTrue(resultado.caminhoCriticoIds().contains(4L));
		assertTrue(resultado.caminhoCriticoIds().contains(2L));
		assertTrue(resultado.caminhoCriticoIds().contains(1L));
	}
}
