package br.edu.malhaia.application.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai JSON estruturado da resposta do LLM, removendo fences markdown
 * e caindo para texto seguro se o parse falhar.
 */
public final class LlmRespostaExtractor {

	private static final Pattern FENCE = Pattern.compile(
			"```(?:json)?\\s*([\\s\\S]*?)```",
			Pattern.CASE_INSENSITIVE
	);

	private final ObjectMapper objectMapper;

	public LlmRespostaExtractor(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public record OrientacaoExtraida(
			String resumo,
			List<String> ordemSugerida,
			List<String> proximosPassos,
			List<String> alertas,
			boolean estruturado
	) {
	}

	public OrientacaoExtraida extrairOrientacao(String bruto, String fallbackResumo) {
		String limpo = limpar(bruto);
		try {
			JsonNode root = objectMapper.readTree(limpo);
			String resumo = texto(root, "resumo");
			if (resumo == null || resumo.isBlank()) {
				resumo = fallbackResumo;
			}
			return new OrientacaoExtraida(
					resumo,
					lista(root, "ordemSugerida"),
					lista(root, "proximosPassos"),
					lista(root, "alertas"),
					true
			);
		} catch (Exception ignored) {
			String texto = limpo == null || limpo.isBlank() ? fallbackResumo : limpo;
			return new OrientacaoExtraida(texto, List.of(), List.of(), List.of(), false);
		}
	}

	public String limpar(String bruto) {
		if (bruto == null) {
			return "";
		}
		String t = bruto.trim();
		Matcher m = FENCE.matcher(t);
		if (m.find()) {
			return m.group(1).trim();
		}
		int start = t.indexOf('{');
		int end = t.lastIndexOf('}');
		if (start >= 0 && end > start) {
			return t.substring(start, end + 1).trim();
		}
		return t;
	}

	private static String texto(JsonNode root, String field) {
		JsonNode n = root.get(field);
		return n == null || n.isNull() ? null : n.asText();
	}

	private static List<String> lista(JsonNode root, String field) {
		JsonNode n = root.get(field);
		if (n == null || !n.isArray()) {
			return List.of();
		}
		List<String> out = new ArrayList<>();
		for (JsonNode item : n) {
			String v = item.asText("").trim();
			if (!v.isBlank()) {
				out.add(v);
			}
		}
		return List.copyOf(out);
	}
}
