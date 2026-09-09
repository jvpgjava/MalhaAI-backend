package br.edu.malhaia.domain.exception;

import java.util.UUID;

public class UsuarioNaoEncontradoException extends RuntimeException {

	public UsuarioNaoEncontradoException(String message) {
		super(message);
	}

	public UsuarioNaoEncontradoException(UUID id) {
		super("Usuário não encontrado: " + id);
	}
}
