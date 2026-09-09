package br.edu.malhaia.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Embedding é gerenciado via {@code JdbcTemplate} no VectorStore adapter (pgvector).
 */
@Entity
@Table(name = "documento_chunk")
public class DocumentoChunkJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "documento_id", nullable = false)
	private Long documentoId;

	@Column(nullable = false)
	private String titulo;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String trecho;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(Long documentoId) {
		this.documentoId = documentoId;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTrecho() {
		return trecho;
	}

	public void setTrecho(String trecho) {
		this.trecho = trecho;
	}
}
