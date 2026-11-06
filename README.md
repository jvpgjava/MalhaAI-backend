# MalhaIA — Backend

Planejador de grade curricular: grafo de pré-requisitos, caminho crítico, menor caminho, oferta semestral, elegibilidade ao REA e camadas de IA (explicação + RAG).

Este guia cobre **somente execução local na máquina**.

---

## 1. O que instalar na máquina

| Ferramenta | Versão recomendada | Para quê |
|---|---|---|
| **JDK** | 25 (LTS) | Compilar e rodar a aplicação |
| **Maven** | 3.9+ | Build (`mvn`) |
| **PostgreSQL** | 16+ (18 ok) | Banco de dados |
| **pgvector** | extensão `vector` no Postgres | Busca por similaridade (RAG) |
| **psql** | vem com o Postgres | Criar banco e extensão |
| **Git** | qualquer recente | Clonar o repositório |

### Conferir se já está instalado (PowerShell)

```powershell
java -version
mvn -version
psql --version
```

### PostgreSQL + pgvector

1. Instale o [PostgreSQL](https://www.postgresql.org/download/windows/) e anote a senha do usuário `postgres`.
2. Garanta que o serviço do Postgres está rodando.
3. Instale a extensão **pgvector** no seu Postgres (o pacote precisa estar disponível no servidor). Depois, no banco do projeto, a extensão é ativada com `CREATE EXTENSION vector` (o script da seção 2 faz isso).

Para testar se a extensão existe no servidor:

```powershell
psql -U postgres -h localhost -c "SELECT * FROM pg_available_extensions WHERE name = 'vector';"
```

Se não listar `vector`, instale o pgvector no Postgres antes de continuar.

---

## 2. Preparar o banco

Na raiz do repositório:

```powershell
cd C:\Users\joaog\MalhaAI-backend
psql -U postgres -h localhost -f scripts\setup-local-db.sql
```

O script:

- cria o banco `malhaia` (ignore o erro se ele já existir);
- ativa a extensão `vector`;
- cria o role `malhaia_app` (opcional; a app por padrão usa `postgres`).

Conferir:

```powershell
psql -U postgres -h localhost -d malhaia -c "\dx"
```

Deve aparecer a extensão `vector`.

As tabelas e o seed da matriz curricular entram sozinhos na **primeira subida** da aplicação, via **Flyway**.

---

## 3. Variáveis de ambiente (via `.env`)

**Não hardcode chaves** em `application.yml` nem em commits. Use um `.env` local:

```powershell
cd C:\Users\joaog\MalhaAI-backend
Copy-Item .env.example .env
# edite .env e preencha ABACUS_API_KEY / GOOGLE_API_KEY / DB_PASSWORD etc.
```

O `.env` e `application-local.yml` estão no `.gitignore`.

### Subir com o `.env` carregado

```powershell
.\scripts\run-local.ps1
```

Isso lê o `.env`, exporta as variáveis na sessão e roda `mvn spring-boot:run`.

### Alternativa manual (sem o script)

```powershell
# exporte as vars do .env na sessão, ou:
$env:ABACUS_API_KEY = "sua_chave"
$env:GOOGLE_API_KEY = "sua_chave"
$env:FRONTEND_ORIGIN = "http://localhost:4200"
mvn spring-boot:run
```

Sem as chaves de IA, o núcleo (grafo, auth, REA, oferta) sobe normalmente; rotas de explicação/RAG respondem **503**.

Depois de subir com as chaves, professor/coordenação/DEV pode indexar:

```powershell
# POST http://localhost:8080/api/documentos/indexar
```

### Variáveis úteis

| Variável | Default | Uso |
|---|---|---|
| `DB_HOST` | `localhost` | Host do Postgres |
| `DB_PORT` | `5432` | Porta |
| `DB_NAME` | `malhaia` | Nome do banco |
| `DB_USER` | `postgres` | Usuário |
| `DB_PASSWORD` | `postgres` | Senha |
| `PORT` | `8080` | Porta HTTP da API |
| `JWT_SECRET` | (valor longo no yml) | Assinatura do JWT — troque em produção |
| `FRONTEND_ORIGIN` | `http://localhost:4200` | Origem CORS do frontend Angular |
| `ABACUS_API_KEY` / `ABACUSAI_API_KEY` | vazio | LLM (explicação / resposta RAG) |
| `ABACUS_MODEL_NAME` | `claude-sonnet-5` | Modelo na RouteLLM |
| `GOOGLE_API_KEY` | vazio | Embeddings Gemini |
| `GEMINI_MODEL_NAME` | `gemini-embedding-001` | Modelo de embedding (768 dims) |

---

## 4. Subir a aplicação

Na raiz do repositório:

```powershell
cd C:\Users\joaog\MalhaAI-backend
.\scripts\run-local.ps1
# ou, sem .env: mvn spring-boot:run
```

Espere a mensagem `Started MalhaiaBackendApplication`.

Health check:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

Deve retornar `status: UP`.

### Testes automatizados

```powershell
mvn test
```

---

## 5. Acessar o Swagger

Com a app no ar, abra no navegador:

**UI interativa**

- [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

(atalho que redireciona: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html))

**OpenAPI JSON**

- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Como autenticar no Swagger

1. Chame `POST /api/auth/cadastro` (ou `/api/auth/login`) e copie o `token` da resposta.
2. Clique em **Authorize** (cadeado no topo da página).
3. Em **Value**, cole: `Bearer <seu_token>`  
   (a palavra `Bearer`, um espaço, e o JWT).
4. Confirme. As rotas protegidas passam a enviar o header automaticamente.

Cadastro de exemplo (corpo JSON):

```json
{
  "email": "aluno@teste.com",
  "senha": "senha123",
  "papel": "ALUNO"
}
```

Cadastro público (`POST /api/auth/cadastro`) aceita somente `ALUNO`.  
Papéis `PROFESSOR`, `COORDENACAO` e `DEV` são criados por `POST /api/admin/usuarios` (somente `DEV`).

---

## 6. Smoke test rápido (PowerShell)

```powershell
$base = "http://localhost:8080"

# Cadastro
$body = @{ email = "aluno@teste.com"; senha = "senha123"; papel = "ALUNO" } | ConvertTo-Json
$auth = Invoke-RestMethod -Method POST -Uri "$base/api/auth/cadastro" -ContentType "application/json" -Body $body
$token = $auth.token
$headers = @{ Authorization = "Bearer $token" }

# Grafo
Invoke-RestMethod -Uri "$base/api/grafo" -Headers $headers

# Caminho crítico
Invoke-RestMethod -Uri "$base/api/grafo/caminho-critico" -Headers $headers
```

### RAG (só com `GOOGLE_API_KEY` + `ABACUS_API_KEY`)

1. Logue um usuário `PROFESSOR`, `COORDENACAO` ou `DEV`.
2. `POST /api/documentos/indexar` (gera embeddings dos chunks seed).
3. `POST /api/perguntas` com `{ "pergunta": "O que é REA?" }`.

### Papéis

| Papel | Capacidades |
|---|---|
| `ALUNO` | Grafo, progresso, eletiva, REA, explicação/perguntas |
| `PROFESSOR` | Consulta + `POST /api/documentos/indexar` |
| `COORDENACAO` | Professor + `POST /api/oferta` + REA de outros alunos |
| `DEV` | Acesso total + `POST /api/admin/usuarios` |

---

## 7. Endpoints principais

| Método | Rota | Auth |
|---|---|---|
| `POST` | `/api/auth/cadastro` | público (só `ALUNO`) |
| `POST` | `/api/auth/login` | público |
| `POST` | `/api/admin/usuarios` | JWT + `DEV` |
| `GET` | `/actuator/health` | público |
| `GET` | `/api/grafo` | JWT |
| `GET` | `/api/grafo/caminho-critico` | JWT |
| `GET` | `/api/grafo/caminho?destino={id}` | JWT |
| `POST` | `/api/disciplinas` | JWT |
| `POST` | `/api/pre-requisitos` | JWT |
| `POST` | `/api/oferta` | JWT + `COORDENACAO` / `DEV` |
| `GET` / `PUT` | `/api/aluno/progresso` | JWT |
| `GET` | `/api/aluno/rea` | JWT (próprio aluno) |
| `GET` | `/api/aluno/{usuarioId}/rea` | JWT + `COORDENACAO` / `DEV` |
| `GET` | `/api/explicacao/{disciplinaId}` | JWT (+ Abacus) |
| `POST` | `/api/documentos/indexar` | JWT + `PROFESSOR` / `COORDENACAO` / `DEV` (+ Gemini) |
| `POST` | `/api/perguntas` | JWT (+ Gemini + Abacus) |

Planejamento detalhado das fases: [`README_BACKEND.md`](README_BACKEND.md).

---

## 8. Problemas comuns

| Sintoma | O que checar |
|---|---|
| Falha ao conectar no banco | Postgres rodando? `DB_PASSWORD` correta? Banco `malhaia` existe? |
| Erro na migration com `vector` | Extensão `pgvector` instalada e `\dx` mostra `vector`? |
| `401` em quase tudo | Faltou `Authorization: Bearer ...` |
| `403` em `/api/oferta` | Usuário precisa ser `COORDENACAO` ou `DEV` |
| `403` em `/api/admin/**` | Usuário precisa ser `DEV` |
| `403` em cadastro com outro papel | Cadastro público só aceita `ALUNO` |
| `503` em explicação/perguntas | `ABACUS_API_KEY` / `GOOGLE_API_KEY` não configuradas |
| Porta 8080 em uso | Pare o outro processo ou use `$env:PORT = "8081"` |
