package br.edu.malhaia.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "aluno_progresso_disciplina")
@IdClass(AlunoProgressoDisciplinaJpaEntity.Pk.class)
public class AlunoProgressoDisciplinaJpaEntity {

	@Id
	@Column(name = "usuario_id", nullable = false)
	private UUID usuarioId;

	@Id
	@Column(name = "disciplina_id", nullable = false)
	private Long disciplinaId;

	public AlunoProgressoDisciplinaJpaEntity() {
	}

	public AlunoProgressoDisciplinaJpaEntity(UUID usuarioId, Long disciplinaId) {
		this.usuarioId = usuarioId;
		this.disciplinaId = disciplinaId;
	}

	public UUID getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(UUID usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Long getDisciplinaId() {
		return disciplinaId;
	}

	public void setDisciplinaId(Long disciplinaId) {
		this.disciplinaId = disciplinaId;
	}

	public static class Pk implements Serializable {

		private UUID usuarioId;
		private Long disciplinaId;

		public Pk() {
		}

		public Pk(UUID usuarioId, Long disciplinaId) {
			this.usuarioId = usuarioId;
			this.disciplinaId = disciplinaId;
		}

		public UUID getUsuarioId() {
			return usuarioId;
		}

		public void setUsuarioId(UUID usuarioId) {
			this.usuarioId = usuarioId;
		}

		public Long getDisciplinaId() {
			return disciplinaId;
		}

		public void setDisciplinaId(Long disciplinaId) {
			this.disciplinaId = disciplinaId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Pk pk)) {
				return false;
			}
			return Objects.equals(usuarioId, pk.usuarioId) && Objects.equals(disciplinaId, pk.disciplinaId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(usuarioId, disciplinaId);
		}
	}
}
