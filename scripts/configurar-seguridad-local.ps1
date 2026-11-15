#Requires -Version 7.0
$ErrorActionPreference = 'Stop'

$repoPath = Split-Path -Parent $PSScriptRoot
$configPath = Join-Path $repoPath 'src/main/liberty/config'
$envPath = Join-Path $configPath 'server.env'
if (-not (Test-Path -LiteralPath $envPath)) {
    Copy-Item -LiteralPath (Join-Path $configPath 'server.env.example') -Destination $envPath
}

$lines = [System.Collections.Generic.List[string]]::new()
$lines.AddRange([System.IO.File]::ReadAllLines($envPath))
$changed = $false
foreach ($key in @('GALENOSV_KEYSTORE_PASSWORD', 'GALENOSV_LTPA_PASSWORD')) {
    $indexes = @(0..($lines.Count - 1) | Where-Object { $lines[$_] -match "^\s*$key\s*=" })
    if ($indexes.Count -gt 1) { throw "La variable $key está duplicada en server.env." }
    if ($indexes.Count -eq 1) {
        $value = ($lines[$indexes[0]] -split '=', 2)[1].Trim()
        if ($value -and $value -notmatch '^cambiar') { continue }
    }
    $secret = [Convert]::ToHexString([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
    if ($indexes.Count -eq 1) { $lines[$indexes[0]] = "$key=$secret" }
    else { $lines.Add("$key=$secret") }
    $changed = $true
}
if ($changed) {
    [System.IO.File]::WriteAllLines($envPath, $lines, [System.Text.UTF8Encoding]::new($false))
}
Write-Output 'Claves TLS y LTPA configuradas en server.env (excluido de Git).'
Write-Output 'Las claves existentes y las credenciales de la aplicación y PostgreSQL se conservan.'
