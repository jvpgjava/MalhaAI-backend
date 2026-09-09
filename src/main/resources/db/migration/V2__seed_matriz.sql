-- Matriz curricular fictícia (DAG) — Ciência da Computação (exemplo)
INSERT INTO disciplina (id, nome, semestre_sugerido, carga_horaria) VALUES
    (1,  'Introdução à Computação',           1, 60),
    (2,  'Cálculo I',                         1, 90),
    (3,  'Programação I',                     2, 90),
    (4,  'Cálculo II',                        2, 90),
    (5,  'Estruturas de Dados',               3, 90),
    (6,  'Álgebra Linear',                    3, 60),
    (7,  'Banco de Dados',                    4, 75),
    (8,  'Engenharia de Software',            4, 75),
    (9,  'Sistemas Operacionais',             5, 75),
    (10, 'Redes de Computadores',             5, 75),
    (11, 'Compiladores',                      6, 75),
    (12, 'Trabalho de Conclusão de Curso',    8, 120);

SELECT setval('disciplina_id_seq', (SELECT MAX(id) FROM disciplina));

-- Pré-requisitos (pre_requisito_id → disciplina_id)
INSERT INTO pre_requisito (disciplina_id, pre_requisito_id) VALUES
    (3,  1),   -- Prog I ← Intro
    (4,  2),   -- Cálculo II ← Cálculo I
    (5,  3),   -- ED ← Prog I
    (6,  4),   -- Álgebra ← Cálculo II
    (7,  5),   -- BD ← ED
    (8,  3),   -- ES ← Prog I
    (9,  5),   -- SO ← ED
    (10, 9),   -- Redes ← SO
    (11, 5),   -- Compiladores ← ED
    (11, 6),   -- Compiladores ← Álgebra
    (12, 7),   -- TCC ← BD
    (12, 8),   -- TCC ← ES
    (12, 10);  -- TCC ← Redes

-- Ofertas do semestre corrente (2026.1)
INSERT INTO oferta_semestral (disciplina_id, semestre, vagas, ofertada) VALUES
    (1,  '2026.1', 80, TRUE),
    (2,  '2026.1', 80, TRUE),
    (3,  '2026.1', 60, TRUE),
    (4,  '2026.1', 60, TRUE),
    (5,  '2026.1', 50, TRUE),
    (6,  '2026.1', 50, TRUE),
    (7,  '2026.1', 40, TRUE),
    (8,  '2026.1', 40, TRUE),
    (9,  '2026.1', 40, FALSE),  -- não ofertada neste semestre
    (10, '2026.1', 40, TRUE),
    (11, '2026.1', 30, TRUE),
    (12, '2026.1', 20, TRUE);

-- Conflitos de horário no semestre
INSERT INTO conflito_horario (disciplina_a_id, disciplina_b_id, semestre) VALUES
    (7, 8, '2026.1'),   -- BD x ES
    (10, 11, '2026.1'); -- Redes x Compiladores
