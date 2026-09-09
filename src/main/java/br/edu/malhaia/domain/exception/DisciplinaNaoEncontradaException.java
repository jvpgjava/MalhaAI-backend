package br.edu.malhaia.domain.exception;

public class DisciplinaNaoEncontradaException extends RuntimeException {

	public DisciplinaNaoEncontradaException(String message) {
		super(message);
	}

	public DisciplinaNaoEncontradaException(Long id) {
		super("Disciplina não encontrada: " + id);
	}
}
