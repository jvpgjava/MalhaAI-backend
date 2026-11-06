-- Expande papéis: PROFESSOR e DEV (mantém ALUNO e COORDENACAO)
ALTER TABLE usuario DROP CONSTRAINT IF EXISTS usuario_papel_check;
ALTER TABLE usuario
    ADD CONSTRAINT usuario_papel_check
    CHECK (papel IN ('ALUNO', 'PROFESSOR', 'COORDENACAO', 'DEV'));
