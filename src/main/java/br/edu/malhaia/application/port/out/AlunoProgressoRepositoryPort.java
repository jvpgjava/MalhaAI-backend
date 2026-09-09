package br.edu.malhaia.application.port.out;

import br.edu.malhaia.domain.model.AlunoProgresso;

import java.util.Optional;
import java.util.UUID;

public interface AlunoProgressoRepositoryPort {

	Optional<AlunoProgresso> findByUsuarioId(UUID usuarioId);

	AlunoProgresso save(AlunoProgresso progresso);
}
