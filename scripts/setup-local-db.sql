-- Setup local do PostgreSQL para MalhaIA.
-- Execute como superusuário:
--   psql -U postgres -f scripts/setup-local-db.sql
--
-- Se o banco malhaia já existir, ignore o erro de CREATE DATABASE
-- e rode só a parte de extensão/privilégios conectado a malhaia.

SELECT 'Criando banco malhaia (ignore erro se já existir)...' AS info;
CREATE DATABASE malhaia;

\c malhaia

CREATE EXTENSION IF NOT EXISTS vector;

DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'malhaia_app') THEN
    CREATE ROLE malhaia_app LOGIN PASSWORD 'malhaia_app';
  END IF;
END
$$;

GRANT CONNECT ON DATABASE malhaia TO malhaia_app;
GRANT USAGE, CREATE ON SCHEMA public TO malhaia_app;
GRANT ALL ON ALL TABLES IN SCHEMA public TO malhaia_app;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO malhaia_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO malhaia_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO malhaia_app;
