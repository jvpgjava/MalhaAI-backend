package br.edu.malhaia.adapters.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ApiDtos {

	private ApiDtos() {
	}

	public record DisciplinaRequest(
			@NotBlank String nome,
			@Min(1) int semestreSugerido,
			@Min(1) int cargaHoraria
	) {
	}

	public record DisciplinaResponse(
			Long id,
			String nome,
			int semestreSugerido,
			int cargaHoraria
	) {
	}

	public record PreRequisitoRequest(
			@NotNull Long disciplinaId,
			@NotNull Long preRequisitoId
	) {
	}

	public record PreRequisitoResponse(
			Long id,
			Long disciplinaId,
			Long preRequisitoId
	) {
	}

	public record OfertaRequest(
			@NotNull Long disciplinaId,
			@NotBlank String semestre,
			@Min(0) int vagas,
			boolean ofertada
	) {
	}

	public record OfertaResponse(
			Long id,
			Long disciplinaId,
			String semestre,
			int vagas,
			boolean ofertada
	) {
	}

	public record GrafoResponse(
			List<DisciplinaResponse> disciplinas,
			List<ArestaResponse> arestas
	) {
	}

	public record ArestaResponse(Long preRequisitoId, Long disciplinaId) {
	}

	public record CaminhoCriticoResponse(
			Map<Long, Integer> semestreMinimoPorDisciplina,
			int totalSemestres,
			List<Long> caminhoCriticoIds
	) {
	}

	public record CaminhoResponse(List<Long> caminho) {
	}

	public record ElegibilidadeReaResponse(Long disciplinaId, String motivo) {
	}

	public record ProgressoRequest(@NotNull Set<Long> disciplinasConcluidas) {
	}

	public record ProgressoResponse(UUID usuarioId, Set<Long> disciplinasConcluidas) {
	}

	public record ExplicacaoResponse(String explicacao) {
	}

	public record OrientacaoRequest(
			@NotBlank String semestre,
			Long destinoPrioridadeId
	) {
	}

	public record OrientacaoEstruturadaResponse(
			String resumo,
			List<String> ordemSugerida,
			List<String> proximosPassos,
			List<String> alertas,
			boolean estruturado
	) {
	}

	public record OrientacaoResponse(
			String semestre,
			String modo,
			Long destinoId,
			List<Long> caminhoIds,
			List<String> caminhoNomes,
			List<Long> proximasOfertadasIds,
			List<String> proximasOfertadasNomes,
			Long primeiraDisciplinaId,
			String primeiraDisciplinaNome,
			OrientacaoEstruturadaResponse orientacao,
			boolean iaDisponivel,
			boolean roadmapIndexado,
			List<FonteOrientacaoResponse> fontesConsultadas
	) {
	}

	public record FonteOrientacaoResponse(String titulo, String trecho, double similaridade) {
	}

	public record PerguntaRequest(@NotBlank @Size(max = 2000) String pergunta) {
	}

	public record PerguntaResponse(String resposta, List<FonteResponse> fontes) {
	}

	public record FonteResponse(Long documentoId, String titulo, String trecho, double similaridade) {
	}

	public record ErrorResponse(String message) {
	}
}
