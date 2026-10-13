$ErrorActionPreference = "Stop"

Set-Location -LiteralPath $PSScriptRoot

if (-not $env:DB_USERNAME) {
    $env:DB_USERNAME = "root"
}

if (-not $env:DB_PASSWORD) {
    $secureDbPassword = Read-Host "MySQL password" -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secureDbPassword).Password
}

if (-not $env:ADMIN_USERNAME) {
    $env:ADMIN_USERNAME = "admin"
}

if (-not $env:ADMIN_FULL_NAME) {
    $env:ADMIN_FULL_NAME = "System Administrator"
}

if (-not $env:ADMIN_PASSWORD) {
    $secureAdminPassword = Read-Host "First run only - initial admin password (press Enter if admin already exists)" -AsSecureString
    $adminPassword = [System.Net.NetworkCredential]::new("", $secureAdminPassword).Password
    if ($adminPassword) {
        if ($adminPassword.Length -lt 8) {
            Write-Error "The initial admin password must contain at least 8 characters."
        }
        $env:ADMIN_PASSWORD = $adminPassword
    }
}

Write-Host ""
Write-Host "Starting Stock Management System..." -ForegroundColor Cyan
Write-Host "The login page will open automatically." -ForegroundColor Cyan

$mavenProcess = Start-Process -FilePath "$PSScriptRoot\mvnw.cmd" `
    -ArgumentList "spring-boot:run" `
    -WorkingDirectory $PSScriptRoot `
    -NoNewWindow `
    -PassThru

$applicationUrl = "http://localhost:8080/login"
$opened = $false

for ($attempt = 0; $attempt -lt 90 -and -not $mavenProcess.HasExited; $attempt++) {
    try {
        $response = Invoke-WebRequest -Uri $applicationUrl -UseBasicParsing -TimeoutSec 1
        if ($response.StatusCode -eq 200) {
            Start-Process $applicationUrl
            $opened = $true
            break
        }
    } catch {
        Start-Sleep -Seconds 1
    }
}

if (-not $opened -and -not $mavenProcess.HasExited) {
    Write-Warning "The browser was not opened automatically. Open $applicationUrl manually."
}

Wait-Process -Id $mavenProcess.Id
exit $mavenProcess.ExitCode
