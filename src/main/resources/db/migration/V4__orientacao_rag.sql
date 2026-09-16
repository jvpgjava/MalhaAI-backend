-- Tipagem de fonte para RAG (normas, fontes confiáveis, roadmaps gerados)
ALTER TABLE documento_chunk
    ADD COLUMN IF NOT EXISTS fonte_tipo VARCHAR(32) NOT NULL DEFAULT 'NORMA';

ALTER TABLE documento_chunk
    DROP CONSTRAINT IF EXISTS documento_chunk_fonte_tipo_check;

ALTER TABLE documento_chunk
    ADD CONSTRAINT documento_chunk_fonte_tipo_check
        CHECK (fonte_tipo IN ('NORMA', 'FONTE_CONFIAVEL', 'ROADMAP'));

UPDATE documento_chunk SET fonte_tipo = 'NORMA' WHERE fonte_tipo IS NULL OR fonte_tipo = '';

-- Documento âncora para fontes confiáveis de orientação curricular
INSERT INTO documento_institucional (id, titulo, conteudo) VALUES (
    2,
    'Fontes confiáveis — orientação de percurso curricular',
    'Diretrizes internas MalhaIA para orientar alunos: priorizar disciplinas ofertadas no semestre atual cujos pré-requisitos já foram cumpridos; começar pelas de menor semestre sugerido; não pular pré-requisitos; alinhar o trajeto ao caminho crítico restante; consultar a coordenação quando a oferta não cobrir o trajeto.'
) ON CONFLICT (id) DO NOTHING;

-- Documento âncora para roadmaps gerados (consultas futuras reusam via RAG)
INSERT INTO documento_institucional (id, titulo, conteudo) VALUES (
    3,
    'Roadmaps de percurso gerados pelo MalhaIA',
    'Repositório de orientações de percurso geradas a partir da oferta semestral, progresso do aluno e fontes confiáveis. Usado como contexto RAG em consultas posteriores.'
) ON CONFLICT (id) DO NOTHING;

SELECT setval('documento_institucional_id_seq', (SELECT MAX(id) FROM documento_institucional));

INSERT INTO documento_chunk (documento_id, titulo, trecho, embedding, fonte_tipo) VALUES
(
    2,
    'Prioridade: ofertado + pré-requisitos ok',
    'Regra confiável de montagem de grade: entre as disciplinas ofertadas no semestre, o aluno deve cursar primeiro aquelas cujos pré-requisitos já foram concluídos. Em empate, preferir a de menor semestre sugerido na matriz e, em seguida, as que estão no caminho crítico restante até a formatura.',
    NULL,
    'FONTE_CONFIAVEL'
),
(
    2,
    'Não pular pré-requisitos',
    'Não se deve indicar disciplinas cujo pré-requisito ainda não foi cumprido, mesmo que estejam ofertadas. O roadmap deve respeitar a ordem topológica do grafo curricular.',
    NULL,
    'FONTE_CONFIAVEL'
),
(
    2,
    'Oferta insuficiente',
    'Se nenhuma disciplina do trajeto restante estiver ofertada no semestre consultado, o aluno deve ser alertado a falar com a coordenação ou escolher outro semestre de referência, em vez de inventar ofertas.',
    NULL,
    'FONTE_CONFIAVEL'
);
