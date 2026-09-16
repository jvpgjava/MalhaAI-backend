-- Tipagem WEB_BUSCA para resultados de busca allowlist indexados no RAG
ALTER TABLE documento_chunk
    DROP CONSTRAINT IF EXISTS documento_chunk_fonte_tipo_check;

ALTER TABLE documento_chunk
    ADD CONSTRAINT documento_chunk_fonte_tipo_check
        CHECK (fonte_tipo IN ('NORMA', 'FONTE_CONFIAVEL', 'ROADMAP', 'WEB_BUSCA'));

INSERT INTO documento_institucional (id, titulo, conteudo) VALUES (
    4,
    'Buscas web confiáveis — orientação tecnológica',
    'Repositório de trechos obtidos de domínios allowlist usados como contexto RAG nas orientações de percurso por disciplina.'
) ON CONFLICT (id) DO NOTHING;

SELECT setval('documento_institucional_id_seq', (SELECT MAX(id) FROM documento_institucional));
