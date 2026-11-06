package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ErrorResponse;
import br.edu.malhaia.domain.exception.AcessoNegadoException;
import br.edu.malhaia.domain.exception.DisciplinaNaoEncontradaException;
import br.edu.malhaia.domain.exception.EmailJaCadastradoException;
import br.edu.malhaia.domain.exception.GrafoCiclicoException;
import br.edu.malhaia.domain.exception.LlmIndisponivelException;
import br.edu.malhaia.domain.exception.UsuarioNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DisciplinaNaoEncontradaException.class)
	public ResponseEntity<ErrorResponse> notFound(DisciplinaNaoEncontradaException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse("Recurso não encontrado"));
	}

	@ExceptionHandler(UsuarioNaoEncontradoException.class)
	public ResponseEntity<ErrorResponse> usuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse("Recurso não encontrado"));
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ErrorResponse> conflito(EmailJaCadastradoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse("Não foi possível concluir o cadastro com estes dados"));
	}

	@ExceptionHandler({AcessoNegadoException.class, AccessDeniedException.class})
	public ResponseEntity<ErrorResponse> acessoNegado(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErrorResponse("Acesso negado"));
	}

	@ExceptionHandler(LlmIndisponivelException.class)
	public ResponseEntity<ErrorResponse> llmIndisponivel(LlmIndisponivelException ex) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ErrorResponse("Serviço de IA temporariamente indisponível"));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> unauthorized(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("Credenciais inválidas"));
	}

	@ExceptionHandler({GrafoCiclicoException.class, IllegalArgumentException.class})
	public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex) {
		String msg = ex.getMessage();
		if (msg == null || msg.isBlank() || msg.length() > 200) {
			msg = "Requisição inválida";
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(msg));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> dataIntegrity(DataIntegrityViolationException ex) {
		log.warn("Integridade de dados: {}", ex.getMostSpecificCause().getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErrorResponse("Operação conflita com dados existentes"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
		FieldError fieldError = ex.getBindingResult().getFieldError();
		String message = fieldError != null && fieldError.getDefaultMessage() != null
				? fieldError.getDefaultMessage()
				: "Dados inválidos";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> generico(Exception ex) {
		log.error("Erro não tratado", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro interno"));
	}
}
