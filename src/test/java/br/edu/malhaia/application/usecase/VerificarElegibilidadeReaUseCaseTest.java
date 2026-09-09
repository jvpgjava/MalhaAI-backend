package br.edu.malhaia.application.usecase;

import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.application.port.out.ConflitoHorarioRepositoryPort;
import br.edu.malhaia.application.port.out.DisciplinaRepositoryPort;
import br.edu.malhaia.application.port.out.OfertaSemestralRepositoryPort;
import br.edu.malhaia.domain.model.AlunoProgresso;
import br.edu.malhaia.domain.model.ConflitoHorario;
import br.edu.malhaia.domain.model.Disciplina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificarElegibilidadeReaUseCaseTest {

	@Mock
	private DisciplinaRepositoryPort disciplinaRepository;
	@Mock
	private AlunoProgressoRepositoryPort alunoProgressoRepository;
	@Mock
	private OfertaSemestralRepositoryPort ofertaRepository;
	@Mock
	private ConflitoHorarioRepositoryPort conflitoRepository;

	private VerificarElegibilidadeReaUseCase useCase;

	private final UUID usuarioId = UUID.fromString("11111111-1111-1111-1111-111111111111");

	@BeforeEach
	void setUp() {
		useCase = new VerificarElegibilidadeReaUseCase(
				disciplinaRepository,
				alunoProgressoRepository,
				ofertaRepository,
				conflitoRepository
		);
	}

	@Test
	void motivoNaoOfertadaQuandoFaltaUmaDisciplinaNaoOfertada() {
		List<Disciplina> todas = List.of(
				new Disciplina(1L, "A", 1, 60),
				new Disciplina(2L, "B", 2, 60),
				new Disciplina(3L, "C", 3, 60)
		);
		when(disciplinaRepository.findAll()).thenReturn(todas);
		when(alunoProgressoRepository.findByUsuarioId(usuarioId))
				.thenReturn(Optional.of(new AlunoProgresso(usuarioId, Set.of(1L, 2L))));
		when(ofertaRepository.findDisciplinaIdsOfertadas(isNull())).thenReturn(Set.of(1L, 2L));
		when(conflitoRepository.findAll()).thenReturn(List.of());

		List<ElegibilidadeRea> resultado = useCase.executar(usuarioId);

		assertEquals(1, resultado.size());
		assertEquals(3L, resultado.getFirst().disciplinaId());
		assertEquals(ElegibilidadeRea.MOTIVO_NAO_OFERTADA, resultado.getFirst().motivo());
	}

	@Test
	void motivoConflitoHorarioQuandoDuasPendenciasConflitam() {
		List<Disciplina> todas = List.of(
				new Disciplina(1L, "A", 1, 60),
				new Disciplina(2L, "B", 2, 60),
				new Disciplina(3L, "C", 3, 60)
		);
		when(disciplinaRepository.findAll()).thenReturn(todas);
		when(alunoProgressoRepository.findByUsuarioId(usuarioId))
				.thenReturn(Optional.of(new AlunoProgresso(usuarioId, Set.of(1L))));
		when(ofertaRepository.findDisciplinaIdsOfertadas(isNull())).thenReturn(Set.of(1L, 2L, 3L));
		when(conflitoRepository.findAll()).thenReturn(List.of(
				new ConflitoHorario(10L, 2L, 3L, "2026.1")
		));

		List<ElegibilidadeRea> resultado = useCase.executar(usuarioId);

		assertEquals(2, resultado.size());
		assertTrue(resultado.stream().allMatch(e ->
				ElegibilidadeRea.MOTIVO_CONFLITO_HORARIO.equals(e.motivo())));
		assertTrue(resultado.stream().anyMatch(e -> e.disciplinaId().equals(2L)));
		assertTrue(resultado.stream().anyMatch(e -> e.disciplinaId().equals(3L)));
	}

	@Test
	void naoDisparaComTresOuMaisPendentes() {
		List<Disciplina> todas = List.of(
				new Disciplina(1L, "A", 1, 60),
				new Disciplina(2L, "B", 2, 60),
				new Disciplina(3L, "C", 3, 60),
				new Disciplina(4L, "D", 4, 60)
		);
		when(disciplinaRepository.findAll()).thenReturn(todas);
		when(alunoProgressoRepository.findByUsuarioId(usuarioId))
				.thenReturn(Optional.of(new AlunoProgresso(usuarioId, Set.of(1L))));

		List<ElegibilidadeRea> resultado = useCase.executar(usuarioId);

		assertTrue(resultado.isEmpty());
	}
}
