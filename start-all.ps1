param(
    [switch]$RebuildBackend
)

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendDir = Join-Path $ProjectRoot "backend"
$FrontendDir = Join-Path $ProjectRoot "frontend"
$LogDir = Join-Path $ProjectRoot "logs"

$JavaExe = "D:\JDK17\jdk-17.0.19+10\bin\java.exe"
$MavenCmd = "D:\Maven\apache-maven-3.9.15\bin\mvn.cmd"
$NpmCmd = "D:\node\npm.cmd"
$MySqlD = "D:\MySQL\mysql-8.4.0-winx64\bin\mysqld.exe"
$MyIni = "D:\MySQL\my.ini"
$BackendJar = Join-Path $BackendDir "target\lab-reservation-system.jar"

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

function Test-PortListening([int]$Port) {
    return [bool](Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
}

function Wait-Port([int]$Port, [string]$Name, [int]$Seconds = 40) {
    for ($i = 0; $i -lt $Seconds; $i++) {
        if (Test-PortListening $Port) {
            Write-Host "$Name is running on port $Port." -ForegroundColor Green
            return
        }
        Start-Sleep -Seconds 1
    }
    throw "$Name start timeout: port $Port is not listening."
}

function Assert-File([string]$Path, [string]$Name) {
    if (-not (Test-Path $Path)) {
        throw "$Name not found: $Path"
    }
}

Assert-File $JavaExe "Java"
Assert-File $MavenCmd "Maven"
Assert-File $NpmCmd "npm"
Assert-File $MySqlD "mysqld"
Assert-File $MyIni "MySQL config"

$env:JAVA_HOME = "D:\JDK17\jdk-17.0.19+10"
$env:MAVEN_HOME = "D:\Maven\apache-maven-3.9.15"
$env:MYSQL_HOME = "D:\MySQL\mysql-8.4.0-winx64"
$env:Path = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:MYSQL_HOME\bin;D:\node;$env:Path"

Write-Host "=== Starting Lab Reservation System ===" -ForegroundColor Cyan

if (Test-PortListening 3306) {
    Write-Host "MySQL is already running." -ForegroundColor Green
} else {
    Write-Host "Starting MySQL..." -ForegroundColor Yellow
    Start-Process -FilePath $MySqlD -ArgumentList "--defaults-file=$MyIni" -WindowStyle Hidden
    Wait-Port 3306 "MySQL" 45
}

if ($RebuildBackend -or -not (Test-Path $BackendJar)) {
    Write-Host "Packaging backend..." -ForegroundColor Yellow
    Push-Location $BackendDir
    try {
        & $MavenCmd package -DskipTests
    } finally {
        Pop-Location
    }
}

if (Test-PortListening 8080) {
    Write-Host "Backend is already running." -ForegroundColor Green
} else {
    Write-Host "Starting backend..." -ForegroundColor Yellow
    $backendCommand = "& `"$JavaExe`" -jar `"$BackendJar`""
    Start-Process -FilePath "powershell.exe" `
        -WorkingDirectory $BackendDir `
        -ArgumentList @("-NoExit", "-ExecutionPolicy", "Bypass", "-Command", $backendCommand)
    Wait-Port 8080 "Backend" 60
}

$NodeModules = Join-Path $FrontendDir "node_modules"
if (-not (Test-Path $NodeModules)) {
    Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
    Push-Location $FrontendDir
    try {
        & $NpmCmd install
    } finally {
        Pop-Location
    }
}

if (Test-PortListening 5173) {
    Write-Host "Frontend is already running." -ForegroundColor Green
} else {
    Write-Host "Starting frontend..." -ForegroundColor Yellow
    $frontendCommand = "& `"$NpmCmd`" run dev"
    Start-Process -FilePath "powershell.exe" `
        -WorkingDirectory $FrontendDir `
        -ArgumentList @("-NoExit", "-ExecutionPolicy", "Bypass", "-Command", $frontendCommand)
    Wait-Port 5173 "Frontend" 60
}

Start-Process "http://localhost:5173"
Write-Host "Done. Open http://localhost:5173" -ForegroundColor Green
