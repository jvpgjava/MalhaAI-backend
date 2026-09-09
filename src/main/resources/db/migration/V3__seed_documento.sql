INSERT INTO documento_institucional (id, titulo, conteudo) VALUES (
    1,
    'Regimento Acadêmico Fictício — Estudo Especial e REA',
    'Art. 42. O Regime de Exercícios Domiciliares Alternativos (REA), também denominado estudo especial, poderá ser concedido ao discente que, faltando uma ou duas disciplinas para a conclusão do curso, não possa cursá-las em regime regular por ausência de oferta no semestre ou por conflito de horário insanável com outra disciplina pendente.

Art. 43. A solicitação de REA deve ser protocolada junto à Coordenação do Curso até o décimo dia útil do período letivo, acompanhada de comprovação do histórico acadêmico e da justificativa. O deferimento cabe à Coordenação, ouvido o colegiado quando necessário. O conteúdo e a forma de avaliação do estudo especial serão definidos pelo docente responsável pela disciplina.'
);

SELECT setval('documento_institucional_id_seq', (SELECT MAX(id) FROM documento_institucional));

-- Chunks sem embedding (preenchidos em runtime via ingestion/embedding)
INSERT INTO documento_chunk (documento_id, titulo, trecho, embedding) VALUES
    (1,
     'REA — elegibilidade',
     'Art. 42. O Regime de Exercícios Domiciliares Alternativos (REA), também denominado estudo especial, poderá ser concedido ao discente que, faltando uma ou duas disciplinas para a conclusão do curso, não possa cursá-las em regime regular por ausência de oferta no semestre ou por conflito de horário insanável com outra disciplina pendente.',
     NULL),
    (1,
     'REA — solicitação',
     'Art. 43. A solicitação de REA deve ser protocolada junto à Coordenação do Curso até o décimo dia útil do período letivo, acompanhada de comprovação do histórico acadêmico e da justificativa. O deferimento cabe à Coordenação, ouvido o colegiado quando necessário. O conteúdo e a forma de avaliação do estudo especial serão definidos pelo docente responsável pela disciplina.',
     NULL);
