package br.edu.malhaia.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "oferta_semestral", uniqueConstraints = @UniqueConstraint(columnNames = {"disciplina_id", "semestre"}))
public class OfertaSemestralJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "disciplina_id", nullable = false)
	private Long disciplinaId;

	@Column(nullable = false, length = 32)
	private String semestre;

	@Column(nullable = false)
	private int vagas;

	@Column(nullable = false)
	private boolean ofertada = true;

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

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public int getVagas() {
		return vagas;
	}

	public void setVagas(int vagas) {
		this.vagas = vagas;
	}

	public boolean isOfertada() {
		return ofertada;
	}

	public void setOfertada(boolean ofertada) {
		this.ofertada = ofertada;
	}
}
