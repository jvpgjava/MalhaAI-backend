CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE disciplina (
    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(255) NOT NULL,
    semestre_sugerido INT          NOT NULL,
    carga_horaria     INT          NOT NULL
);

CREATE TABLE pre_requisito (
    id               BIGSERIAL PRIMARY KEY,
    disciplina_id    BIGINT NOT NULL REFERENCES disciplina (id),
    pre_requisito_id BIGINT NOT NULL REFERENCES disciplina (id),
    UNIQUE (disciplina_id, pre_requisito_id)
);

CREATE TABLE oferta_semestral (
    id            BIGSERIAL PRIMARY KEY,
    disciplina_id BIGINT       NOT NULL REFERENCES disciplina (id),
    semestre      VARCHAR(32)  NOT NULL,
    vagas         INT          NOT NULL,
    ofertada      BOOLEAN      NOT NULL DEFAULT TRUE,
    UNIQUE (disciplina_id, semestre)
);

CREATE TABLE conflito_horario (
    id              BIGSERIAL PRIMARY KEY,
    disciplina_a_id BIGINT      NOT NULL REFERENCES disciplina (id),
    disciplina_b_id BIGINT      NOT NULL REFERENCES disciplina (id),
    semestre        VARCHAR(32) NOT NULL
);

CREATE TABLE usuario (
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    senha_hash VARCHAR(255)        NOT NULL,
    papel      VARCHAR(32)         NOT NULL,
    CONSTRAINT usuario_papel_check CHECK (papel IN ('ALUNO', 'COORDENACAO'))
);

CREATE TABLE aluno_progresso_disciplina (
    usuario_id    UUID   NOT NULL REFERENCES usuario (id),
    disciplina_id BIGINT NOT NULL REFERENCES disciplina (id),
    PRIMARY KEY (usuario_id, disciplina_id)
);

CREATE TABLE documento_institucional (
    id       BIGSERIAL PRIMARY KEY,
    titulo   VARCHAR(255) NOT NULL,
    conteudo TEXT         NOT NULL
);

CREATE TABLE documento_chunk (
    id           BIGSERIAL PRIMARY KEY,
    documento_id BIGINT       NOT NULL REFERENCES documento_institucional (id),
    titulo       VARCHAR(255) NOT NULL,
    trecho       TEXT         NOT NULL,
    embedding    vector(768)
);

CREATE INDEX documento_chunk_embedding_hnsw_idx
    ON documento_chunk USING hnsw (embedding vector_cosine_ops);
