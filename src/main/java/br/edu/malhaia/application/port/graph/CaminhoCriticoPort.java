package br.edu.malhaia.application.port.graph;

import br.edu.malhaia.domain.model.GrafoCurricular;

public interface CaminhoCriticoPort {

	CaminhoCriticoResultado calcular(GrafoCurricular grafo);
}
