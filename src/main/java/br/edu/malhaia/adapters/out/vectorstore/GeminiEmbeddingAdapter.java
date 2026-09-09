package br.edu.malhaia.adapters.out.vectorstore;

import br.edu.malhaia.application.port.out.EmbeddingPort;
import br.edu.malhaia.config.MalhaiaProperties;
import br.edu.malhaia.domain.exception.LlmIndisponivelException;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.springframework.stereotype.Component;

@Component
public class GeminiEmbeddingAdapter implements EmbeddingPort {

	private final EmbeddingModel embeddingModel;

	public GeminiEmbeddingAdapter(MalhaiaProperties properties) {
		String apiKey = properties.getGemini().getApiKey();
		if (apiKey == null || apiKey.isBlank()) {
			this.embeddingModel = null;
		} else {
			this.embeddingModel = GoogleAiEmbeddingModel.builder()
					.apiKey(apiKey)
					.modelName(properties.getGemini().getModelName())
					.outputDimensionality(768)
					.build();
		}
	}

	@Override
	public float[] embed(String texto) {
		if (embeddingModel == null) {
			throw new LlmIndisponivelException("GOOGLE_API_KEY não configurada para embeddings");
		}
		try {
			Response<Embedding> response = embeddingModel.embed(texto);
			if (response == null || response.content() == null) {
				throw new LlmIndisponivelException("Embedding vazio");
			}
			return response.content().vector();
		} catch (LlmIndisponivelException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new LlmIndisponivelException("Serviço de embedding indisponível", e);
		}
	}
}
