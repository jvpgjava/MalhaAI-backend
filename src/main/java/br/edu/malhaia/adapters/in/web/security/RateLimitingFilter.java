package br.edu.malhaia.adapters.in.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limit simples em memória: 20 req/min por IP para explicação e perguntas.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private static final int MAX_REQUESTS = 20;
	private static final long WINDOW_MS = 60_000L;

	private final Map<String, Deque<Long>> requestsByIp = new ConcurrentHashMap<>();

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return !(path.startsWith("/api/explicacao") || path.startsWith("/api/perguntas"));
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String ip = clientIp(request);
		long now = Instant.now().toEpochMilli();
		Deque<Long> timestamps = requestsByIp.computeIfAbsent(ip, key -> new ArrayDeque<>());
		synchronized (timestamps) {
			while (!timestamps.isEmpty() && now - timestamps.peekFirst() > WINDOW_MS) {
				timestamps.pollFirst();
			}
			if (timestamps.size() >= MAX_REQUESTS) {
				response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
				response.setContentType("application/json");
				response.getWriter().write("{\"message\":\"Limite de requisições excedido. Tente novamente em breve.\"}");
				return;
			}
			timestamps.addLast(now);
		}
		filterChain.doFilter(request, response);
	}

	private static String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
