package br.edu.malhaia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "malhaia")
public class MalhaiaProperties {

	private final Jwt jwt = new Jwt();
	private final Cors cors = new Cors();
	private final Abacus abacus = new Abacus();
	private final Gemini gemini = new Gemini();

	public Jwt getJwt() {
		return jwt;
	}

	public Cors getCors() {
		return cors;
	}

	public Abacus getAbacus() {
		return abacus;
	}

	public Gemini getGemini() {
		return gemini;
	}

	public static class Jwt {
		private String secret = "change-me-to-a-long-secret-at-least-256-bits-long-for-hs256!!";
		private long expirationMs = 86_400_000L;

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public long getExpirationMs() {
			return expirationMs;
		}

		public void setExpirationMs(long expirationMs) {
			this.expirationMs = expirationMs;
		}
	}

	public static class Cors {
		private String allowedOrigin = "http://localhost:4200";

		public String getAllowedOrigin() {
			return allowedOrigin;
		}

		public void setAllowedOrigin(String allowedOrigin) {
			this.allowedOrigin = allowedOrigin;
		}
	}

	public static class Abacus {
		private String apiKey = "";
		private String baseUrl = "https://routellm.abacus.ai/v1";
		private String modelName = "claude-sonnet-5";

		public String getApiKey() {
			return apiKey;
		}

		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getModelName() {
			return modelName;
		}

		public void setModelName(String modelName) {
			this.modelName = normalizeModelName(modelName);
		}

		private static String normalizeModelName(String modelName) {
			if (modelName == null || modelName.isBlank()) {
				return "claude-sonnet-5";
			}
			String normalized = modelName.trim().toLowerCase().replace('_', '-');
			return switch (normalized) {
				case "claude-sonnet", "claude-sonnet-4", "sonnet" -> "claude-sonnet-5";
				default -> normalized;
			};
		}
	}

	public static class Gemini {
		private String apiKey = "";
		private String modelName = "gemini-embedding-001";

		public String getApiKey() {
			return apiKey;
		}

		public void setApiKey(String apiKey) {
			this.apiKey = apiKey;
		}

		public String getModelName() {
			return modelName;
		}

		public void setModelName(String modelName) {
			this.modelName = modelName;
		}
	}
}
