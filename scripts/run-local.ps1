# Carrega .env na sessão e sobe o Spring Boot (sem hardcode de secrets no YAML).
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$envFile = Join-Path $root '.env'
if (-not (Test-Path $envFile)) {
  Write-Host "Arquivo .env nao encontrado. Copie .env.example para .env e preencha as chaves." -ForegroundColor Yellow
  exit 1
}

Get-Content $envFile | ForEach-Object {
  $line = $_.Trim()
  if (-not $line -or $line.StartsWith('#')) { return }
  $idx = $line.IndexOf('=')
  if ($idx -lt 1) { return }
  $key = $line.Substring(0, $idx).Trim()
  $value = $line.Substring($idx + 1).Trim()
  if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
    $value = $value.Substring(1, $value.Length - 2)
  }
  Set-Item -Path "Env:$key" -Value $value
}

Write-Host "Variaveis carregadas de .env — iniciando Spring Boot..." -ForegroundColor Cyan
mvn spring-boot:run
