package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.graph.CaminhoCriticoPort;
import br.edu.malhaia.application.port.graph.CaminhoCriticoResultado;
import br.edu.malhaia.domain.model.GrafoCurricular;
import org.springframework.stereotype.Service;

@Service
public class CalcularCaminhoCriticoUseCase {

	private final CarregarGrafoUseCase carregarGrafoUseCase;
	private final CaminhoCriticoPort caminhoCriticoPort;

	public CalcularCaminhoCriticoUseCase(
			CarregarGrafoUseCase carregarGrafoUseCase,
			CaminhoCriticoPort caminhoCriticoPort
	) {
		this.carregarGrafoUseCase = carregarGrafoUseCase;
		this.caminhoCriticoPort = caminhoCriticoPort;
	}

	public CaminhoCriticoResultado executar(String semestreOferta) {
		GrafoCurricular grafo = carregarGrafoUseCase.executar(semestreOferta);
		return caminhoCriticoPort.calcular(grafo);
	}

	public CaminhoCriticoResultado executar() {
		return executar(null);
	}
}
