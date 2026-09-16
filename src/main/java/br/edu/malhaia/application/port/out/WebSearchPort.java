package br.edu.malhaia.application.port.out;

import java.util.List;

public interface WebSearchPort {

	record ResultadoBusca(String titulo, String url, String trecho) {
	}

	List<ResultadoBusca> buscar(String consulta, int limite);
}
