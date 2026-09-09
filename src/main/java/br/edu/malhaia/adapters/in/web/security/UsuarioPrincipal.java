package br.edu.malhaia.adapters.in.web.security;

import br.edu.malhaia.domain.model.Papel;
import br.edu.malhaia.domain.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UsuarioPrincipal implements UserDetails {

	private final UUID id;
	private final String email;
	private final String senhaHash;
	private final Papel papel;

	public UsuarioPrincipal(Usuario usuario) {
		this.id = usuario.id();
		this.email = usuario.email();
		this.senhaHash = usuario.senhaHash();
		this.papel = usuario.papel();
	}

	public UUID getId() {
		return id;
	}

	public Papel getPapel() {
		return papel;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + papel.name()));
	}

	@Override
	public String getPassword() {
		return senhaHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
