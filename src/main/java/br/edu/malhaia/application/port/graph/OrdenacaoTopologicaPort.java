package br.edu.malhaia.application.port.graph;

import br.edu.malhaia.domain.exception.GrafoCiclicoException;
import br.edu.malhaia.domain.model.GrafoCurricular;

import java.util.List;

public interface OrdenacaoTopologicaPort {

	List<Long> ordenar(GrafoCurricular grafo) throws GrafoCiclicoException;
}
