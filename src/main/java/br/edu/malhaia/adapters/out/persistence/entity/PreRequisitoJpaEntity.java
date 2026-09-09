package br.edu.malhaia.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "pre_requisito", uniqueConstraints = @UniqueConstraint(columnNames = {"disciplina_id", "pre_requisito_id"}))
public class PreRequisitoJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "disciplina_id", nullable = false)
	private Long disciplinaId;

	@Column(name = "pre_requisito_id", nullable = false)
	private Long preRequisitoId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDisciplinaId() {
		return disciplinaId;
	}

	public void setDisciplinaId(Long disciplinaId) {
		this.disciplinaId = disciplinaId;
	}

	public Long getPreRequisitoId() {
		return preRequisitoId;
	}

	public void setPreRequisitoId(Long preRequisitoId) {
		this.preRequisitoId = preRequisitoId;
	}
}
