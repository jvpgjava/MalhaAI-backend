package br.edu.malhaia.adapters.out.persistence.mapper;

import br.edu.malhaia.adapters.out.persistence.entity.ConflitoHorarioJpaEntity;
import br.edu.malhaia.adapters.out.persistence.entity.DisciplinaJpaEntity;
import br.edu.malhaia.adapters.out.persistence.entity.OfertaSemestralJpaEntity;
import br.edu.malhaia.adapters.out.persistence.entity.PreRequisitoJpaEntity;
import br.edu.malhaia.adapters.out.persistence.entity.UsuarioJpaEntity;
import br.edu.malhaia.domain.model.ConflitoHorario;
import br.edu.malhaia.domain.model.Disciplina;
import br.edu.malhaia.domain.model.OfertaSemestral;
import br.edu.malhaia.domain.model.Papel;
import br.edu.malhaia.domain.model.PreRequisito;
import br.edu.malhaia.domain.model.Usuario;

public final class PersistenceMapper {

	private PersistenceMapper() {
	}

	public static Disciplina toDomain(DisciplinaJpaEntity entity) {
		return new Disciplina(entity.getId(), entity.getNome(), entity.getSemestreSugerido(), entity.getCargaHoraria());
	}

	public static DisciplinaJpaEntity toEntity(Disciplina disciplina) {
		DisciplinaJpaEntity entity = new DisciplinaJpaEntity();
		entity.setId(disciplina.id());
		entity.setNome(disciplina.nome());
		entity.setSemestreSugerido(disciplina.semestreSugerido());
		entity.setCargaHoraria(disciplina.cargaHoraria());
		return entity;
	}

	public static PreRequisito toDomain(PreRequisitoJpaEntity entity) {
		return new PreRequisito(entity.getId(), entity.getDisciplinaId(), entity.getPreRequisitoId());
	}

	public static PreRequisitoJpaEntity toEntity(PreRequisito preRequisito) {
		PreRequisitoJpaEntity entity = new PreRequisitoJpaEntity();
		entity.setId(preRequisito.id());
		entity.setDisciplinaId(preRequisito.disciplinaId());
		entity.setPreRequisitoId(preRequisito.preRequisitoId());
		return entity;
	}

	public static OfertaSemestral toDomain(OfertaSemestralJpaEntity entity) {
		return new OfertaSemestral(
				entity.getId(),
				entity.getDisciplinaId(),
				entity.getSemestre(),
				entity.getVagas(),
				entity.isOfertada()
		);
	}

	public static OfertaSemestralJpaEntity toEntity(OfertaSemestral oferta) {
		OfertaSemestralJpaEntity entity = new OfertaSemestralJpaEntity();
		entity.setId(oferta.id());
		entity.setDisciplinaId(oferta.disciplinaId());
		entity.setSemestre(oferta.semestre());
		entity.setVagas(oferta.vagas());
		entity.setOfertada(oferta.ofertada());
		return entity;
	}

	public static ConflitoHorario toDomain(ConflitoHorarioJpaEntity entity) {
		return new ConflitoHorario(
				entity.getId(),
				entity.getDisciplinaAId(),
				entity.getDisciplinaBId(),
				entity.getSemestre()
		);
	}

	public static Usuario toDomain(UsuarioJpaEntity entity) {
		return new Usuario(
				entity.getId(),
				entity.getEmail(),
				entity.getSenhaHash(),
				Papel.valueOf(entity.getPapel())
		);
	}

	public static UsuarioJpaEntity toEntity(Usuario usuario) {
		UsuarioJpaEntity entity = new UsuarioJpaEntity();
		entity.setId(usuario.id());
		entity.setEmail(usuario.email());
		entity.setSenhaHash(usuario.senhaHash());
		entity.setPapel(usuario.papel().name());
		return entity;
	}
}
