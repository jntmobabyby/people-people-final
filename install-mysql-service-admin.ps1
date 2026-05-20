$ErrorActionPreference = "Stop"

$ServiceName = "MySQL84Lab"
$MySqlD = "D:\MySQL\mysql-8.4.0-winx64\bin\mysqld.exe"
$MyIni = "D:\MySQL\my.ini"

$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $isAdmin) {
    throw "Run PowerShell as Administrator before executing this script."
}

if (Get-Service -Name $ServiceName -ErrorAction SilentlyContinue) {
    Write-Host "$ServiceName service already exists." -ForegroundColor Green
} else {
    & $MySqlD --install $ServiceName "--defaults-file=$MyIni"
    Write-Host "$ServiceName service installed." -ForegroundColor Green
}

Set-Service -Name $ServiceName -StartupType Automatic
Start-Service -Name $ServiceName
Get-Service -Name $ServiceName | Select-Object Name, Status, StartType
