package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.out.EmbeddingPort;
import br.edu.malhaia.application.port.out.VectorStorePort;
import br.edu.malhaia.application.port.out.VectorStorePort.ChunkPendente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gera embeddings Gemini para chunks institucionais ainda sem vetor (Fase 9).
 */
@Service
public class IndexarDocumentosUseCase {

	private final VectorStorePort vectorStorePort;
	private final EmbeddingPort embeddingPort;

	public IndexarDocumentosUseCase(VectorStorePort vectorStorePort, EmbeddingPort embeddingPort) {
		this.vectorStorePort = vectorStorePort;
		this.embeddingPort = embeddingPort;
	}

	@Transactional
	public int executar() {
		List<ChunkPendente> pendentes = vectorStorePort.listarSemEmbedding();
		for (ChunkPendente chunk : pendentes) {
			float[] embedding = embeddingPort.embed(chunk.trecho());
			vectorStorePort.atualizarEmbedding(chunk.id(), embedding);
		}
		return pendentes.size();
	}
}
