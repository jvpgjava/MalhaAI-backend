package br.edu.malhaia.adapters.out.llm;

import br.edu.malhaia.application.port.out.LlmPort;
import br.edu.malhaia.config.MalhaiaProperties;
import br.edu.malhaia.domain.exception.LlmIndisponivelException;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Component
public class AbacusLlmAdapter implements LlmPort {

	private final ChatModel chatModel;

	public AbacusLlmAdapter(MalhaiaProperties properties) {
		String apiKey = properties.getAbacus().getApiKey();
		if (apiKey == null || apiKey.isBlank()) {
			this.chatModel = null;
		} else {
			this.chatModel = OpenAiChatModel.builder()
					.apiKey(apiKey)
					.baseUrl(properties.getAbacus().getBaseUrl())
					.modelName(properties.getAbacus().getModelName())
					.build();
		}
	}

	@Override
	public String gerarTexto(String systemPrompt, String userPrompt) {
		if (chatModel == null) {
			throw new LlmIndisponivelException("ABACUS_API_KEY não configurada");
		}
		try {
			ChatResponse response = chatModel.chat(
					SystemMessage.from(systemPrompt),
					UserMessage.from(userPrompt)
			);
			if (response == null || response.aiMessage() == null || response.aiMessage().text() == null) {
				throw new LlmIndisponivelException("Resposta vazia do LLM");
			}
			return response.aiMessage().text();
		} catch (LlmIndisponivelException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new LlmIndisponivelException("Serviço de LLM indisponível", e);
		}
	}
}
