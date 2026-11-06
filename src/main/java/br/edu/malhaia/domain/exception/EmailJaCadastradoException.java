package br.edu.malhaia.domain.exception;

public class EmailJaCadastradoException extends RuntimeException {

	public EmailJaCadastradoException() {
		super("Não foi possível concluir o cadastro com estes dados");
	}

	public EmailJaCadastradoException(String ignoredEmail) {
		this();
	}
}
