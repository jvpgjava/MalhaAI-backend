package br.edu.malhaia.adapters.in.web.security;

import br.edu.malhaia.domain.exception.AcessoNegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static UsuarioPrincipal currentPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioPrincipal principal)) {
			throw new AcessoNegadoException("Usuário não autenticado");
		}
		return principal;
	}

	public static UUID currentUserId() {
		return currentPrincipal().getId();
	}
}
