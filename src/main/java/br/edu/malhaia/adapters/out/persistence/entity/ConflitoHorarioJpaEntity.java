package br.edu.malhaia.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conflito_horario")
public class ConflitoHorarioJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "disciplina_a_id", nullable = false)
	private Long disciplinaAId;

	@Column(name = "disciplina_b_id", nullable = false)
	private Long disciplinaBId;

	@Column(nullable = false, length = 32)
	private String semestre;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDisciplinaAId() {
		return disciplinaAId;
	}

	public void setDisciplinaAId(Long disciplinaAId) {
		this.disciplinaAId = disciplinaAId;
	}

	public Long getDisciplinaBId() {
		return disciplinaBId;
	}

	public void setDisciplinaBId(Long disciplinaBId) {
		this.disciplinaBId = disciplinaBId;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}
}
