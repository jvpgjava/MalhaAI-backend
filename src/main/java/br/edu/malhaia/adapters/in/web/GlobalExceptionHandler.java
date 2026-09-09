package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ErrorResponse;
import br.edu.malhaia.domain.exception.AcessoNegadoException;
import br.edu.malhaia.domain.exception.DisciplinaNaoEncontradaException;
import br.edu.malhaia.domain.exception.EmailJaCadastradoException;
import br.edu.malhaia.domain.exception.GrafoCiclicoException;
import br.edu.malhaia.domain.exception.LlmIndisponivelException;
import br.edu.malhaia.domain.exception.UsuarioNaoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DisciplinaNaoEncontradaException.class)
	public ResponseEntity<ErrorResponse> notFound(DisciplinaNaoEncontradaException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(UsuarioNaoEncontradoException.class)
	public ResponseEntity<ErrorResponse> usuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ErrorResponse> conflito(EmailJaCadastradoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler({AcessoNegadoException.class, AccessDeniedException.class})
	public ResponseEntity<ErrorResponse> acessoNegado(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(LlmIndisponivelException.class)
	public ResponseEntity<ErrorResponse> llmIndisponivel(LlmIndisponivelException ex) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> unauthorized(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler({GrafoCiclicoException.class, IllegalArgumentException.class})
	public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> dataIntegrity(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse("Violação de integridade de dados"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
		FieldError fieldError = ex.getBindingResult().getFieldError();
		String message = fieldError != null
				? fieldError.getField() + ": " + fieldError.getDefaultMessage()
				: "Dados inválidos";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> generico(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro interno"));
	}
}
