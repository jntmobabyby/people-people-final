param(
    [switch]$IncludeMySql
)

$ErrorActionPreference = "Continue"

function Stop-PortOwner([int]$Port, [string]$Name) {
    $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if (-not $connections) {
        Write-Host "$Name is not running." -ForegroundColor DarkGray
        return
    }

    $pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($pidValue in $pids) {
        try {
            $proc = Get-Process -Id $pidValue -ErrorAction Stop
            Stop-Process -Id $pidValue -Force
            Write-Host "Stopped ${Name}: PID $pidValue ($($proc.ProcessName))" -ForegroundColor Green
        } catch {
            Write-Host "Failed to stop ${Name}: PID $pidValue, $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}

Write-Host "=== Stopping Lab Reservation System ===" -ForegroundColor Cyan
Stop-PortOwner 5173 "Frontend"
Stop-PortOwner 8080 "Backend"

if ($IncludeMySql) {
    Stop-PortOwner 3306 "MySQL"
} else {
    Write-Host "MySQL is left running. To stop it too, run: .\stop-all.ps1 -IncludeMySql" -ForegroundColor Yellow
}
