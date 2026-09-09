package br.edu.malhaia.domain.exception;

public class LlmIndisponivelException extends RuntimeException {

	public LlmIndisponivelException(String message) {
		super(message);
	}

	public LlmIndisponivelException(String message, Throwable cause) {
		super(message, cause);
	}
}
