package br.edu.malhaia.application.config;

import br.edu.malhaia.application.llm.LlmRespostaExtractor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public LlmRespostaExtractor llmRespostaExtractor(ObjectMapper objectMapper) {
		return new LlmRespostaExtractor(objectMapper);
	}
}
