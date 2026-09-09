package br.edu.malhaia.adapters.in.web;

import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ElegibilidadeReaResponse;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ProgressoRequest;
import br.edu.malhaia.adapters.in.web.dto.ApiDtos.ProgressoResponse;
import br.edu.malhaia.adapters.in.web.security.SecurityUtils;
import br.edu.malhaia.application.port.out.AlunoProgressoRepositoryPort;
import br.edu.malhaia.application.usecase.ElegibilidadeRea;
import br.edu.malhaia.application.usecase.VerificarElegibilidadeReaUseCase;
import br.edu.malhaia.domain.model.AlunoProgresso;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/aluno")
public class AlunoController {

	private final VerificarElegibilidadeReaUseCase verificarElegibilidadeReaUseCase;
	private final AlunoProgressoRepositoryPort alunoProgressoRepository;

	public AlunoController(
			VerificarElegibilidadeReaUseCase verificarElegibilidadeReaUseCase,
			AlunoProgressoRepositoryPort alunoProgressoRepository
	) {
		this.verificarElegibilidadeReaUseCase = verificarElegibilidadeReaUseCase;
		this.alunoProgressoRepository = alunoProgressoRepository;
	}

	@GetMapping("/rea")
	public List<ElegibilidadeReaResponse> reaProprio(@RequestParam(required = false) String semestre) {
		UUID usuarioId = SecurityUtils.currentUserId();
		return mapRea(verificarElegibilidadeReaUseCase.executar(usuarioId, semestre));
	}

	@GetMapping("/{usuarioId}/rea")
	@PreAuthorize("hasRole('COORDENACAO')")
	public List<ElegibilidadeReaResponse> reaDeAluno(
			@PathVariable UUID usuarioId,
			@RequestParam(required = false) String semestre
	) {
		return mapRea(verificarElegibilidadeReaUseCase.executar(usuarioId, semestre));
	}

	@GetMapping("/progresso")
	public ProgressoResponse obterProgresso() {
		UUID usuarioId = SecurityUtils.currentUserId();
		Set<Long> disciplinas = alunoProgressoRepository.findByUsuarioId(usuarioId)
				.map(AlunoProgresso::disciplinasConcluidas)
				.orElse(Set.of());
		return new ProgressoResponse(usuarioId, disciplinas);
	}

	@PutMapping("/progresso")
	public ProgressoResponse atualizarProgresso(@Valid @RequestBody ProgressoRequest request) {
		UUID usuarioId = SecurityUtils.currentUserId();
		AlunoProgresso salvo = alunoProgressoRepository.save(
				new AlunoProgresso(usuarioId, new HashSet<>(request.disciplinasConcluidas()))
		);
		return new ProgressoResponse(salvo.usuarioId(), salvo.disciplinasConcluidas());
	}

	private static List<ElegibilidadeReaResponse> mapRea(List<ElegibilidadeRea> lista) {
		return lista.stream()
				.map(e -> new ElegibilidadeReaResponse(e.disciplinaId(), e.motivo()))
				.toList();
	}
}
