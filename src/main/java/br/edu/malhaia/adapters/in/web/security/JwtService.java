package br.edu.malhaia.adapters.in.web.security;

import br.edu.malhaia.config.MalhaiaProperties;
import br.edu.malhaia.domain.model.Papel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(MalhaiaProperties properties) {
		byte[] keyBytes = properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		this.expirationMs = properties.getJwt().getExpirationMs();
	}

	public String gerarToken(UUID usuarioId, String email, Papel papel) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + expirationMs);
		return Jwts.builder()
				.subject(usuarioId.toString())
				.claim("email", email)
				.claim("papel", papel.name())
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(secretKey)
				.compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public UUID extrairUsuarioId(String token) {
		return UUID.fromString(parseClaims(token).getSubject());
	}

	public String extrairEmail(String token) {
		return parseClaims(token).get("email", String.class);
	}

	public Papel extrairPapel(String token) {
		return Papel.valueOf(parseClaims(token).get("papel", String.class));
	}

	public boolean isTokenValido(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (RuntimeException e) {
			return false;
		}
	}
}
