package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.application.usecase.IndexarDocumentosUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

	private final IndexarDocumentosUseCase indexarDocumentosUseCase;

	public DocumentoController(IndexarDocumentosUseCase indexarDocumentosUseCase) {
		this.indexarDocumentosUseCase = indexarDocumentosUseCase;
	}

	@PostMapping("/indexar")
	@ResponseStatus(HttpStatus.OK)
	@PreAuthorize("hasRole('COORDENACAO')")
	public Map<String, Object> indexar() {
		int indexados = indexarDocumentosUseCase.executar();
		return Map.of("chunksIndexados", indexados);
	}
}
