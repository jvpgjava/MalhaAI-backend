package br.edu.malhaia.adapters.out.vectorstore;

import br.edu.malhaia.application.port.out.TrechoRecuperado;
import br.edu.malhaia.application.port.out.VectorStorePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class PgVectorStoreAdapter implements VectorStorePort {

	private final JdbcTemplate jdbcTemplate;

	public PgVectorStoreAdapter(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void indexar(Long documentoId, String titulo, String chunk, float[] embedding) {
		String vectorLiteral = toVectorLiteral(embedding);
		jdbcTemplate.update(
				"""
						INSERT INTO documento_chunk (documento_id, titulo, trecho, embedding)
						VALUES (?, ?, ?, CAST(? AS vector))
						""",
				documentoId,
				titulo,
				chunk,
				vectorLiteral
		);
	}

	@Override
	public List<TrechoRecuperado> buscarSimilares(float[] embedding, int k) {
		String vectorLiteral = toVectorLiteral(embedding);
		return jdbcTemplate.query(
				"""
						SELECT documento_id, titulo, trecho,
						       (1 - (embedding <=> CAST(? AS vector))) AS similaridade
						FROM documento_chunk
						WHERE embedding IS NOT NULL
						ORDER BY embedding <=> CAST(? AS vector)
						LIMIT ?
						""",
				(rs, rowNum) -> new TrechoRecuperado(
						rs.getLong("documento_id"),
						rs.getString("titulo"),
						rs.getString("trecho"),
						rs.getDouble("similaridade")
				),
				vectorLiteral,
				vectorLiteral,
				k
		);
	}

	@Override
	public List<ChunkPendente> listarSemEmbedding() {
		return jdbcTemplate.query(
				"""
						SELECT id, documento_id, titulo, trecho
						FROM documento_chunk
						WHERE embedding IS NULL
						ORDER BY id
						""",
				(rs, rowNum) -> new ChunkPendente(
						rs.getLong("id"),
						rs.getLong("documento_id"),
						rs.getString("titulo"),
						rs.getString("trecho")
				)
		);
	}

	@Override
	public void atualizarEmbedding(long chunkId, float[] embedding) {
		String vectorLiteral = toVectorLiteral(embedding);
		jdbcTemplate.update(
				"""
						UPDATE documento_chunk
						SET embedding = CAST(? AS vector)
						WHERE id = ?
						""",
				vectorLiteral,
				chunkId
		);
	}

	static String toVectorLiteral(float[] embedding) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < embedding.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(String.format(Locale.US, "%.8f", embedding[i]));
		}
		sb.append(']');
		return sb.toString();
	}
}
