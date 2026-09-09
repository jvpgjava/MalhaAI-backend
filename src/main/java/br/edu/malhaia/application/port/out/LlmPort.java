package br.edu.malhaia.application.port.out;

public interface LlmPort {

	String gerarTexto(String systemPrompt, String userPrompt);
}
