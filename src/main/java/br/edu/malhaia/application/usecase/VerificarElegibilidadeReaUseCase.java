package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.application.port.out.ConflitoHorarioRepositoryPort;
import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.AlunoProgresso;
import br.edu.malhaia.domain.model.ConflitoHorario;
import br.edu.malhaia.domain.model.Disciplina;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Regra REA: faltam 1 ou 2 disciplinas para formatura E (disciplina pendente
 * não ofertada OU conflita horário com outra pendência).
 */
@Service
public class VerificarElegibilidadeReaUseCase {

	private final DisciplinaRepositoryPort disciplinaRepository;
	private final AlunoProgressoRepositoryPort alunoProgressoRepository;
	private final OfertaSemestralRepositoryPort ofertaRepository;
	private final ConflitoHorarioRepositoryPort conflitoRepository;

	public VerificarElegibilidadeReaUseCase(
			DisciplinaRepositoryPort disciplinaRepository,
			AlunoProgressoRepositoryPort alunoProgressoRepository,
			OfertaSemestralRepositoryPort ofertaRepository,
			ConflitoHorarioRepositoryPort conflitoRepository
	) {
		this.disciplinaRepository = disciplinaRepository;
		this.alunoProgressoRepository = alunoProgressoRepository;
		this.ofertaRepository = ofertaRepository;
		this.conflitoRepository = conflitoRepository;
	}

	public List<ElegibilidadeRea> executar(UUID usuarioId, String semestre) {
		List<Disciplina> todas = disciplinaRepository.findAll();
		Set<Long> todasIds = todas.stream().map(Disciplina::id).collect(Collectors.toSet());

		Set<Long> concluidas = alunoProgressoRepository.findByUsuarioId(usuarioId)
				.map(AlunoProgresso::disciplinasConcluidas)
				.orElse(Set.of());
		if (concluidas == null) {
			concluidas = Set.of();
		}

		Set<Long> pendentes = new HashSet<>(todasIds);
		pendentes.removeAll(concluidas);

		if (pendentes.size() < 1 || pendentes.size() > 2) {
			return List.of();
		}

		Set<Long> ofertadas = ofertaRepository.findDisciplinaIdsOfertadas(semestre);
		List<ConflitoHorario> conflitos = semestre != null
				? conflitoRepository.findBySemestre(semestre)
				: conflitoRepository.findAll();

		List<ElegibilidadeRea> resultado = new ArrayList<>();
		for (Long pendenteId : pendentes) {
			if (!ofertadas.contains(pendenteId)) {
				resultado.add(new ElegibilidadeRea(pendenteId, ElegibilidadeRea.MOTIVO_NAO_OFERTADA));
				continue;
			}
			if (temConflitoComOutraPendencia(pendenteId, pendentes, conflitos)) {
				resultado.add(new ElegibilidadeRea(pendenteId, ElegibilidadeRea.MOTIVO_CONFLITO_HORARIO));
			}
		}
		return resultado;
	}

	public List<ElegibilidadeRea> executar(UUID usuarioId) {
		return executar(usuarioId, null);
	}

	private boolean temConflitoComOutraPendencia(
			Long disciplinaId,
			Set<Long> pendentes,
			List<ConflitoHorario> conflitos
	) {
		for (ConflitoHorario conflito : conflitos) {
			Long a = conflito.disciplinaAId();
			Long b = conflito.disciplinaBId();
			if (disciplinaId.equals(a) && pendentes.contains(b)) {
				return true;
			}
			if (disciplinaId.equals(b) && pendentes.contains(a)) {
				return true;
			}
		}
		return false;
	}
}
