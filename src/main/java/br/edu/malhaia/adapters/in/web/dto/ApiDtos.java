package br.edu.malhaia.adapters.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ApiDtos {

	private static final String SEMESTRE_MSG = "Semestre inválido. Use o formato YYYY.N (ex.: 2026.1)";
	private static final String SEMESTRE_REGEX = "^\\d{4}[./\\-][12]$";

	private ApiDtos() {
	}

	public record DisciplinaRequest(
			@NotBlank(message = "Informe o nome da disciplina")
			@Size(min = 2, max = 200, message = "Nome da disciplina inválido")
			String nome,
			@Min(value = 1, message = "Semestre sugerido inválido")
			@Max(value = 20, message = "Semestre sugerido inválido")
			int semestreSugerido,
			@Min(value = 1, message = "Carga horária inválida")
			@Max(value = 1000, message = "Carga horária inválida")
			int cargaHoraria
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
			@NotNull(message = "Informe a disciplina")
			Long disciplinaId,
			@NotBlank(message = SEMESTRE_MSG)
			@Pattern(regexp = SEMESTRE_REGEX, message = SEMESTRE_MSG)
			String semestre,
			@Min(value = 0, message = "Vagas não podem ser negativas")
			@Max(value = 10000, message = "Vagas acima do limite")
			int vagas,
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

	public record ProgressoRequest(
			@NotNull(message = "Informe as disciplinas concluídas")
			@Size(max = 500, message = "Lista de progresso acima do limite")
			Set<Long> disciplinasConcluidas
	) {
	}

	public record ProgressoResponse(UUID usuarioId, Set<Long> disciplinasConcluidas) {
	}

	public record ExplicacaoResponse(String explicacao) {
	}

	public record OrientacaoRequest(
			@NotBlank(message = SEMESTRE_MSG)
			@Pattern(regexp = SEMESTRE_REGEX, message = SEMESTRE_MSG)
			String semestre,
			Long destinoPrioridadeId
	) {
	}

	public record DisciplinaOrientacaoResponse(
			String nome,
			String porqueNessaOrdem,
			String sobre
	) {
	}

	public record OrientacaoEstruturadaResponse(
			String resumo,
			List<String> ordemSugerida,
			List<DisciplinaOrientacaoResponse> disciplinas,
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

	public record PerguntaRequest(
			@NotBlank(message = "Informe a pergunta")
			@Size(min = 3, max = 2000, message = "A pergunta deve ter entre 3 e 2000 caracteres")
			String pergunta
	) {
	}

	public record PerguntaResponse(String resposta, List<FonteResponse> fontes) {
	}

	public record FonteResponse(Long documentoId, String titulo, String trecho, double similaridade) {
	}

	public record ErrorResponse(String message) {
	}
}
