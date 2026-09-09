# MalhaIA — Backend

Planejador de grade curricular que modela o currículo de um curso como um
grafo dirigido acíclico (DAG) de pré-requisitos, calcula o caminho crítico
até a formatura, sugere a rota mais curta até uma disciplina eletiva,
considera a oferta e a capacidade de vaga de cada disciplina por semestre,
sinaliza elegibilidade ao regime de Estudo Especial (REA), e oferece duas
camadas de IA: uma explicação em linguagem natural do resultado do grafo, e
uma busca (RAG) sobre documentos institucionais como o regimento acadêmico e
o PPC.

Este documento está organizado em fases sequenciais. Cada fase tem um
critério de aceite objetivo — não avance para a próxima fase sem ele
cumprido. Se algo aqui parecer ambíguo durante a implementação, pare e
pergunte antes de assumir um comportamento.

## Stack

- Java 25 (LTS) como baseline, compatível com Java 26 se o ambiente de build já tiver o JDK disponível
- Spring Boot 4.1, construído sobre o Spring Framework 7 — primeira geração da 4.x com suporte de primeira classe a Java 25/26, modularização dos jars e null-safety via JSpecify
- Threads virtuais (Project Loom, estável desde o Java 21) para as chamadas de I/O ao LLM e ao banco na camada de IA, em vez de pool de thread tradicional
- Nada de `javax.*` — Spring Boot 3+/4+ já usa o namespace `jakarta.*` por padrão, não introduza nada do mundo antigo do Spring Boot 2
- PostgreSQL, com a extensão `pgvector` habilitada desde já (usada só a
  partir da Fase 9, mas mais simples de já subir no docker-compose inicial)
- Spring Data JPA + Flyway para migrations
- Spring Security + JWT + BCrypt para autenticação — mesmo padrão já
  implementado no DivergIA-backend do grupo, reaproveitar em vez de
  redescobrir
- LangChain4j para integração com o modelo de linguagem, em duas pontas
  separadas: geração de texto via módulo OpenAI-compatible apontado para a
  Abacus.AI/RouteLLM (modelo Claude Sonnet), e embedding via módulo Gemini
  (a Abacus.AI/RouteLLM não oferece embedding, por isso a combinação com
  dois provedores) — ver detalhes nas Fases 7 e 8
- Arquitetura hexagonal (ports & adapters)
- Maven
- JUnit 5 + Mockito

> Nota de versão: confirme no `start.spring.io` a combinação exata de Java e
> Spring Boot disponível no momento em que for gerar o projeto — essas
> versões saem a cada poucos meses, então trate os números acima como "o
> mais recente estável da linha 4.x", não como um número fixo a ser
> perseguido se já tiver saído uma versão mais nova.

## Arquitetura (hexagonal)

- `domain` — entidades e regras de negócio puras (Disciplina, PreRequisito,
  ConflitoHorario, regra de elegibilidade REA). Sem dependência de Spring ou
  de qualquer framework.
- `application` — casos de uso: `CalcularCaminhoCriticoUseCase`,
  `BuscarMenorCaminhoUseCase`, `VerificarElegibilidadeReaUseCase`,
  `GerarExplicacaoUseCase`, `ConsultarNormasUseCase`; e as portas de saída
  `LlmPort` e `VectorStorePort` (`application/port/out`) — nenhum caso de
  uso importa LangChain4j diretamente, só essas interfaces.
- `adapters/in/web` — controllers REST, DTOs de entrada e saída.
- `adapters/out/persistence` — repositórios JPA, mapeamento entidade ↔
  domínio.
- `adapters/out/llm` — implementa `LlmPort` com LangChain4j + módulo
  OpenAI-compatible, apontado para a Abacus.AI/RouteLLM (Fase 8).
- `adapters/out/vectorstore` — implementa `VectorStorePort` com
  LangChain4j + módulo Gemini para embedding, sobre a tabela com a coluna
  `pgvector` (Fase 9).

Regra geral: o pacote `domain` nunca importa nada de `adapters`. Os casos de
uso em `application` dependem de interfaces (ports) que os adapters
implementam — isso inclusive facilita testar os algoritmos de grafo sem
precisar subir banco nem IA.

## Modelo de domínio (visão geral)

- **Disciplina**: id, nome, semestreSugerido, cargaHoraria.
- **PreRequisito**: disciplinaId, preRequisitoId — é a aresta do DAG.
- **OfertaSemestral**: disciplinaId, semestre, vagas, ofertada (booleano).
- **ConflitoHorario**: disciplinaAId, disciplinaBId, semestre — aresta do
  grafo de conflito, não dirigido.
- **AlunoProgresso**: usuarioId (FK para `Usuario`), lista de ids de
  disciplinas concluídas.
- **Usuario**: id (UUID), email, senhaHash (BCrypt), papel (`ALUNO` ou
  `COORDENACAO`).
- **DocumentoInstitucional**: id, titulo, conteudo, embedding (usado só na
  Fase 9).

## Boas práticas de código (SOLID)

A arquitetura hexagonal já força boa parte disso, mas seja explícito nas
decisões de código, não deixe implícito:

- **Responsabilidade única** — cada caso de uso faz uma coisa só.
  `CalcularCaminhoCriticoUseCase` calcula o caminho crítico e não sabe nada
  sobre como isso é persistido ou exposto via REST.
- **Aberto/fechado** — os algoritmos de busca (ordenação topológica,
  Dijkstra) ficam atrás de uma interface na camada de aplicação, de forma
  que adicionar uma nova estratégia de busca no futuro não exija alterar
  quem já a consome.
- **Substituição de Liskov** — qualquer adapter que implemente um port
  cumpre exatamente o contrato esperado, sem exigir que quem chama saiba
  qual implementação concreta está por trás.
- **Segregação de interface** — prefira várias interfaces pequenas e
  específicas (`CaminhoCriticoPort`, `MenorCaminhoPort`) a uma única
  interface grande fazendo tudo relacionado a grafo.
- **Inversão de dependência** — `domain` e `application` nunca importam
  nada de `adapters`; é sempre o adapter que implementa uma interface
  definida pelo núcleo, nunca o contrário — é o princípio que mais importa
  aqui, e já está descrito na seção de arquitetura acima.

Outras práticas gerais esperadas em todo o código: nomes de classe e
método que dizem o que fazem sem precisar abrir o arquivo, métodos curtos,
DTOs como `record` (imutáveis, menos boilerplate), e exceções de domínio
específicas (`GrafoCiclicoException`, `DisciplinaNaoEncontradaException`)
em vez de deixar uma `RuntimeException` genérica estourar até o
controller.

## Identificadores: quando usar UUID e quando não

Não use UUID por padrão em tudo — na maioria das entidades deste projeto
isso é over-engineering e piora performance de índice sem ganho real de
segurança.

- **ID sequencial (`bigint` autoincrement)** para tudo que não é sensível e
  não precisa dificultar adivinhação: `Disciplina`, `PreRequisito`,
  `OfertaSemestral`, `ConflitoHorario`, `DocumentoInstitucional`. Nenhuma
  dessas informações é privada de um aluno específico.
- **UUID** só onde faz diferença de verdade: o identificador de `Usuario`
  (nunca sequencial pra login/conta) e qualquer token de sessão. O
  `AlunoProgresso` não precisa de id próprio — é indexado pelo
  `usuarioId`, que já é UUID por vir de `Usuario`.
- Isso não substitui controle de acesso de verdade — UUID dificulta
  adivinhação, mas não é autorização. O endpoint de progresso do aluno
  ainda precisa checar, pelo token JWT, que quem pede é o dono daquele
  dado (ou tem papel `COORDENACAO`); UUID sozinho não resolve isso.

## Segurança

- Validação de entrada em todo DTO de request (Bean Validation /
  `jakarta.validation`) — nunca confie em dado vindo do frontend sem
  revalidar no backend.
- Toda consulta ao banco via Spring Data JPA/JPQL parametrizado — nunca
  concatenar string pra montar SQL.
- CORS configurado explicitamente para a origem do frontend, nunca `*`.
- Chave de API do provedor de LLM e credencial do banco via variável de
  ambiente, nunca hardcoded nem commitada no repositório — inclua um
  `.env.example` sem valores reais como referência.
- Usuário do banco usado pela aplicação com privilégio mínimo necessário
  (não usar o superusuário do Postgres em produção).
- Limite de taxa (*rate limiting*) nos endpoints de IA
  (`/api/explicacao`, `/api/perguntas`) — são os mais caros de abusar,
  tanto em custo de chamada ao LLM quanto em superfície de negação de
  serviço.
- Tratamento de erro centralizado (`@ControllerAdvice`) que nunca devolve
  stack trace nem detalhe interno de implementação na resposta HTTP.
- Na camada RAG (Fase 9), trate a pergunta do usuário como entrada não
  confiável antes de compor o prompt — isso é superfície de *prompt
  injection*. O sistema não deve deixar uma pergunta manipular o papel do
  assistente ou tentar extrair instruções internas; a resposta deve vir
  só do conteúdo recuperado dos documentos institucionais.

## Banco de dados

- PostgreSQL com a extensão `pgvector` habilitada desde a Fase 1 (mesmo
  que só usada a partir da Fase 9), pra não precisar migrar de banco no
  meio do projeto.
- Migrations via Flyway desde o início — nunca alterar schema à mão em
  produção nem depender de `ddl-auto: update` do Hibernate fora do
  ambiente local de desenvolvimento.
- Índice `ivfflat` ou `hnsw` na coluna de embedding assim que a tabela de
  `DocumentoInstitucional` existir, pra busca por similaridade não ficar
  lenta conforme a base de documentos institucionais cresce.

## Fases de desenvolvimento

### Fase 1 — Base do projeto e modelagem do grafo

- Estrutura hexagonal de pastas conforme acima.
- Entidades de domínio `Disciplina` e `PreRequisito`.
- Construção do grafo em memória a partir de uma lista de pares
  (preRequisito → disciplina), carregada de um arquivo de seed/fixture com
  uma matriz curricular de exemplo (pode ser fictícia nesta fase).
- Endpoint `GET /api/grafo` retornando nós e arestas em JSON.

**Critério de aceite:** subir a aplicação, e o endpoint retornar o grafo de
exemplo corretamente, com todos os nós e arestas esperados.

### Fase 2 — Ordenação topológica e caminho crítico

- Algoritmo de Kahn para ordenação topológica, detectando e reportando
  ciclo como erro tratado (nunca deixar estourar exceção genérica).
- Cálculo do caminho crítico por programação dinâmica sobre a ordenação
  topológica: semestre mínimo de cada disciplina é o maior semestre entre
  seus pré-requisitos, mais um.
- Endpoint `GET /api/grafo/caminho-critico` retornando o semestre mínimo de
  cada disciplina e o total de semestres até a formatura.

**Critério de aceite:** testes unitários cobrindo (a) grafo sem ciclo, (b)
grafo com ciclo retornando erro tratado, (c) um caso de convergência, uma
disciplina com dois pré-requisitos diretos em semestres diferentes.

### Fase 3 — Persistência

- Migrar do seed em memória para PostgreSQL via Spring Data JPA, com
  migrations Flyway.
- Endpoints `POST /api/disciplinas` e `POST /api/pre-requisitos` para
  popular a matriz curricular real (ou um script de carga em lote a partir
  de um CSV, se for mais prático para o grupo alimentar os dados reais).

**Critério de aceite:** os dados persistem entre reinicializações da
aplicação, e o endpoint da Fase 1 continua funcionando lendo do banco.

### Fase 4 — Autenticação e papéis (ALUNO / COORDENACAO)

- Spring Security + JWT + BCrypt, reaproveitando o padrão já implementado
  no DivergIA-backend do grupo (cadastro, login, hash de senha, geração e
  validação de token). Não precisa reimplementar recuperação de senha por
  e-mail nem exclusão de conta — fora de escopo aqui, dá pra resetar senha
  direto no banco se algum professor esquecer a própria até novembro.
- Dois papéis, sem terceiro por enquanto: `ALUNO` (enxerga e gerencia só o
  próprio progresso) e `COORDENACAO` (edita oferta e vaga por semestre —
  cobre tanto coordenador de curso quanto professor com essa
  responsabilidade).
- Endpoints `POST /api/auth/cadastro` e `POST /api/auth/login` (públicos),
  retornando o JWT no login.
- Toda rota que não seja essas duas (nem `/actuator/health` nem a
  documentação Swagger) exige `Authorization: Bearer <token>` a partir
  desta fase em diante.

**Critério de aceite:** cadastrar um usuário de cada papel, logar com os
dois, e confirmar que uma rota protegida responde `401` sem token e `200`
com token válido.

### Fase 5 — Busca de menor caminho (Dijkstra)

- Endpoint `GET /api/grafo/caminho?destino={id}` retornando a sequência
  mínima de pré-requisitos entre as disciplinas já concluídas pelo aluno
  logado (via `AlunoProgresso` do token) e a disciplina de destino — o
  caso de uso é "quero cursar esta eletiva, qual o caminho mais curto a
  partir do que já fiz". Aceitar também um parâmetro `origem` opcional
  para facilitar teste isolado do algoritmo sem depender de progresso
  cadastrado.
- Implementação literal de Dijkstra com fila de prioridade, mesmo o grafo
  sendo não-ponderado (todo peso é 1) — é o algoritmo pedido explicitamente
  na atividade da disciplina, então deve existir de fato, não só o
  equivalente por BFS disfarçado.

**Critério de aceite:** teste unitário comparando o resultado com o
esperado num grafo de exemplo com mais de um caminho possível entre origem
e destino.

### Fase 6 — Oferta semestral e capacidade de vaga

- Entidade `OfertaSemestral`.
- Endpoint `POST /api/oferta`, restrito ao papel `COORDENACAO`, para
  informar, por semestre, quais disciplinas serão ofertadas e quantas
  vagas cada uma tem.
- Os cálculos das Fases 2 e 5 passam a operar sobre o subgrafo de
  disciplinas efetivamente ofertadas no período — uma disciplina não
  ofertada não entra nas arestas consideradas pela busca.

**Critério de aceite:** marcar uma disciplina como não ofertada faz os
endpoints de caminho crítico e de menor caminho recalcularem ignorando
aquele nó; um usuário `ALUNO` tentando chamar `POST /api/oferta` recebe
`403`.

### Fase 7 — Regra do REA

- Regra: quando o aluno está a uma ou duas disciplinas da formatura **e** a
  disciplina pendente não é ofertada **ou** conflita em horário com outra
  pendência, ela é sinalizada como elegível ao regime de Estudo Especial.
- Endpoint `GET /api/aluno/rea` retornando as disciplinas elegíveis do
  aluno logado (identificado pelo token, não por id na URL — evita que um
  aluno peça o REA de outro só trocando o número). Papel `COORDENACAO`
  pode consultar `GET /api/aluno/{usuarioId}/rea` de qualquer aluno.

**Critério de aceite:** teste cobrindo os dois motivos de elegibilidade
separadamente, um teste confirmando que a regra não dispara quando faltam
três ou mais disciplinas, e um teste confirmando que um `ALUNO` não
consegue consultar o REA de outro usuário.

### Fase 8 — Camada de explicação (LangChain4j, sem RAG)

- Implementar `LlmPort` (`application/port/out`) e o adapter
  `adapters/out/llm`, usando o módulo OpenAI-compatible do LangChain4j
  apontado para a Abacus.AI/RouteLLM — mesmo padrão já usado no
  DivergIA-backend do grupo, então reaproveitem o que já aprenderam lá em
  vez de redescobrir a integração do zero.
- Variáveis de ambiente: `ABACUS_API_KEY`, `ABACUS_BASE_URL` (default
  `https://routellm.abacus.ai/v1`), `ABACUS_MODEL_NAME` (default
  `claude-sonnet-5`, ajustar se o modelo disponível na conta for outro).
- Endpoint `GET /api/explicacao/{disciplinaId}` que traduz o resultado do
  grafo (por que aquela disciplina é crítica, quantos semestres ela atrasa
  se reprovada) em uma explicação em texto natural.
- Esta camada é puramente de tradução de um resultado já calculado — o LLM
  não decide nada, só explica o que o grafo já determinou com certeza.

**Critério de aceite:** chamada real à Abacus.AI/RouteLLM retornando
explicação coerente para pelo menos três disciplinas de teste diferentes
(uma crítica, uma fora da cadeia crítica, uma elegível a REA).

### Fase 9 — Camada RAG (documentos institucionais)

- Implementar `VectorStorePort` e o adapter `adapters/out/vectorstore`,
  usando o módulo Gemini do LangChain4j para gerar os embeddings —
  necessário porque a Abacus.AI/RouteLLM não oferece embedding, só geração
  de texto. Mesmo modelo de embedding já validado no DivergIA-backend:
  `gemini-embedding-001`.
- Variáveis de ambiente: `GOOGLE_API_KEY`, `GEMINI_MODEL_NAME` (default
  `gemini-embedding-001`).
- Coluna `pgvector` com **768 dimensões**, para bater com a saída do
  `gemini-embedding-001` — confirme essa dimensão no momento de criar a
  migration, ela é o erro mais comum ao configurar pgvector pela primeira
  vez.
- Ingestão de documentos institucionais (regimento acadêmico, PPC) em
  chunks, com embedding armazenado na coluna acima.
- Endpoint `POST /api/perguntas` recebendo uma pergunta em linguagem
  natural sobre normas institucionais, buscando os chunks mais próximos
  via `VectorStorePort` e retornando resposta (gerada via `LlmPort`, a
  mesma porta da Fase 8) com o trecho-fonte citado.
- Esta camada é separada da Fase 8: aqui a resposta certa não existe em
  lugar nenhum estruturado, por isso a recuperação é necessária. A
  geração de texto em si continua sendo Claude Sonnet via `LlmPort` —
  RAG muda de onde vem o contexto, não qual modelo escreve a resposta.

**Critério de aceite:** perguntar algo presente no documento de teste
carregado e receber resposta correta citando o trecho de onde veio.

### Fase 10 — Testes, documentação e ajustes finais

- Cobertura de teste unitário nos casos de uso principais (não precisa ser
  100%, mas os algoritmos de grafo das Fases 2, 5 e 7 são inegociáveis).
- Documentação OpenAPI/Swagger de todos os endpoints, incluindo o esquema
  de autenticação Bearer JWT (botão "Authorize" funcional no Swagger).
- Revisão de tratamento de erro: grafo cíclico, disciplina inexistente,
  aluno inexistente, LLM indisponível (a aplicação não deve cair se a IA
  falhar — a parte determinística do grafo tem que continuar funcionando
  sozinha).

