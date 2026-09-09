package br.edu.malhaia.application.config;

import br.edu.malhaia.application.graph.CaminhoCriticoCalculator;
import br.edu.malhaia.application.graph.DijkstraMenorCaminho;
import br.edu.malhaia.application.graph.KahnOrdenacaoTopologica;
import br.edu.malhaia.application.port.graph.CaminhoCriticoPort;
import br.edu.malhaia.application.port.graph.MenorCaminhoPort;
import br.edu.malhaia.application.port.graph.OrdenacaoTopologicaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphConfig {

	@Bean
	public OrdenacaoTopologicaPort ordenacaoTopologicaPort() {
		return new KahnOrdenacaoTopologica();
	}

	@Bean
	public CaminhoCriticoPort caminhoCriticoPort(OrdenacaoTopologicaPort ordenacaoTopologicaPort) {
		return new CaminhoCriticoCalculator(ordenacaoTopologicaPort);
	}

	@Bean
	public MenorCaminhoPort menorCaminhoPort() {
		return new DijkstraMenorCaminho();
	}
}
