package br.edu.malhaia.adapters.in.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private static final long WINDOW_MS = 60_000L;

	private final Map<String, Deque<Long>> requestsByKey = new ConcurrentHashMap<>();
	private final boolean trustProxy;

	public RateLimitingFilter(@Value("${malhaia.security.trust-proxy:false}") boolean trustProxy) {
		this.trustProxy = trustProxy;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return bucketFor(request.getRequestURI()) == null;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		Bucket bucket = bucketFor(request.getRequestURI());
		if (bucket == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String key = bucket.name() + "|" + clientIp(request);
		long now = Instant.now().toEpochMilli();
		Deque<Long> timestamps = requestsByKey.computeIfAbsent(key, k -> new ArrayDeque<>());
		synchronized (timestamps) {
			while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MS) {
				timestamps.pollFirst();
			}
			if (timestamps.size() >= bucket.maxPerMinute()) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/json");
				response.getWriter().write(
						"{\"message\":\"Limite de requisições excedido. Tente novamente em breve.\"}"
				);
				return;
			}
			timestamps.addLast(now);
		}
		filterChain.doFilter(request, response);
	}

	private static Bucket bucketFor(String path) {
		if (path == null) {
			return null;
		}
		if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/cadastro")) {
			return Bucket.AUTH;
		}
		if (path.startsWith("/api/admin")) {
			return Bucket.ADMIN;
		}
		if (path.startsWith("/api/documentos")) {
			return Bucket.DOCS;
		}
		if (path.startsWith("/api/explicacao")
				|| path.startsWith("/api/perguntas")
				|| path.startsWith("/api/grafo/orientacao")) {
			return Bucket.LLM;
		}
		return null;
	}

	private String clientIp(HttpServletRequest request) {
		if (trustProxy) {
			String forwarded = request.getHeader("X-Forwarded-For");
			if (forwarded != null && !forwarded.isBlank()) {
				return forwarded.split(",")[0].trim();
			}
		}
		return request.getRemoteAddr();
	}

	private enum Bucket {
		AUTH(10),
		ADMIN(30),
		DOCS(10),
		LLM(20);

		private final int maxPerMinute;

		Bucket(int maxPerMinute) {
			this.maxPerMinute = maxPerMinute;
		}

		int maxPerMinute() {
			return maxPerMinute;
		}
	}
}
