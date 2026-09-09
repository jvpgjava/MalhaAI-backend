package br.edu.malhaia.application.port.out;

import java.util.List;

public interface VectorStorePort {

	void indexar(Long documentoId, String titulo, String chunk, float[] embedding);

	List<TrechoRecuperado> buscarSimilares(float[] embedding, int k);

	List<ChunkPendente> listarSemEmbedding();

	void atualizarEmbedding(long chunkId, float[] embedding);

	record ChunkPendente(long id, long documentoId, String titulo, String trecho) {
	}
}
