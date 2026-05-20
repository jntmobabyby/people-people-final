$MySqlD = "D:\MySQL\mysql-8.4.0-winx64\bin\mysqld.exe"
$MyIni = "D:\MySQL\my.ini"
$running = Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue
if (-not $running) {
    Start-Process -FilePath $MySqlD -ArgumentList "--defaults-file=$MyIni" -WindowStyle Hidden
}
