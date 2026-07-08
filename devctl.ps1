#!/usr/bin/env pwsh
# devctl - developer control script for the Flight Booking API (Windows / PowerShell).
# One entry point for common dev tasks: start / stop / restart the server, build,
# run tests, drive Docker, and view logs.  Run '.\devctl.ps1 help' for usage.

[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [string]$Command = 'help',

    [Parameter(Position = 1, ValueFromRemainingArguments = $true)]
    [string[]]$Rest = @()
)

$ErrorActionPreference = 'Stop'
Set-Location -Path $PSScriptRoot

# --- Configuration -------------------------------------------------------
$AppName      = 'flight-booking'
$Port         = 8081
$DefaultProf  = 'dev'
$BaseUrl      = "http://localhost:$Port"
$LogDir       = Join-Path $PSScriptRoot 'logs'
$AppLog       = Join-Path $LogDir 'flight-booking.log'
$ConsoleOut   = Join-Path $LogDir 'devctl-console.log'
$ConsoleErr   = Join-Path $LogDir 'devctl-console.err.log'
$StateDir     = Join-Path $PSScriptRoot '.devctl'
$PidFile      = Join-Path $StateDir 'server.pid'
$Gradlew      = Join-Path $PSScriptRoot 'gradlew.bat'
$DockerImage  = 'flight-booking'
$DockerName   = 'flight-booking'
$StartTimeout = 90

# --- Option parsing (long + short) --------------------------------------
$SpringProfile = $DefaultProf
$Lines         = 50
$LinesExplicit = $false
$Since         = $null
$Follow        = $false

for ($i = 0; $i -lt $Rest.Count; $i++) {
    switch -Regex ($Rest[$i]) {
        '^(-p|--profile)$' { $SpringProfile = $Rest[++$i] }
        '^(-n|--lines)$'   { $Lines = [int]$Rest[++$i]; $LinesExplicit = $true }
        '^(-s|--since)$'   { $Since = $Rest[++$i] }
        '^(-f|--follow)$'  { $Follow = $true }
        default            { }
    }
}

# --- Helpers -------------------------------------------------------------
function Get-ServerPid {
    try {
        $c = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction Stop |
             Select-Object -First 1
        if ($c) { return [int]$c.OwningProcess }
    } catch {
        $line = netstat -ano | Select-String ":$Port\s.*LISTENING" | Select-Object -First 1
        if ($line) { return [int](($line.ToString().Trim() -split '\s+')[-1]) }
    }
    return $null
}

function Test-Running { return $null -ne (Get-ServerPid) }

function Invoke-Gradle {
    param([string[]]$Tasks)
    & $Gradlew @Tasks
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

function Start-Server {
    if (Test-Running) {
        Write-Host "Already running on $BaseUrl (PID $(Get-ServerPid))." -ForegroundColor Yellow
        return
    }
    New-Item -ItemType Directory -Force -Path $LogDir, $StateDir | Out-Null
    Write-Host "Starting $AppName (profile: $SpringProfile) ..." -ForegroundColor Cyan
    # Launch the Gradle wrapper via cmd from the repo root (relative path avoids space-in-path
    # quoting issues), detached, with stdout/stderr captured for boot diagnostics.
    $proc = Start-Process -FilePath 'cmd.exe' `
        -ArgumentList '/c', '.\gradlew.bat', 'bootRun', "--args=--spring.profiles.active=$SpringProfile" `
        -WorkingDirectory $PSScriptRoot -WindowStyle Hidden -PassThru `
        -RedirectStandardOutput $ConsoleOut -RedirectStandardError $ConsoleErr
    "$($proc.Id)" | Out-File -FilePath $PidFile -Encoding ascii
    Write-Host -NoNewline "Waiting for $BaseUrl "
    for ($t = 0; $t -lt $StartTimeout; $t++) {
        if (Test-Running) {
            Write-Host ""
            Write-Host "Started (PID $(Get-ServerPid)).  Swagger: $BaseUrl/swagger-ui.html" -ForegroundColor Green
            return
        }
        Start-Sleep -Seconds 1
        Write-Host -NoNewline '.'
    }
    Write-Host ""
    Write-Host "Did not open port $Port within ${StartTimeout}s. See $ConsoleErr / $AppLog." -ForegroundColor Red
}

function Stop-Server {
    $spid = Get-ServerPid
    if ($spid) {
        Write-Host "Stopping server (PID $spid) ..." -ForegroundColor Cyan
        Stop-Process -Id $spid -Force -ErrorAction SilentlyContinue
        Write-Host "Stopped." -ForegroundColor Green
    } else {
        Write-Host "Not running (nothing on port $Port)." -ForegroundColor Yellow
    }
    if (Test-Path $PidFile) {
        $launcher = Get-Content $PidFile -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($launcher) { Stop-Process -Id ([int]$launcher) -Force -ErrorAction SilentlyContinue }
        Remove-Item $PidFile -Force -ErrorAction SilentlyContinue
    }
}

function Get-Status {
    $spid = Get-ServerPid
    if ($spid) {
        Write-Host "RUNNING  $BaseUrl  (PID $spid)" -ForegroundColor Green
    } else {
        Write-Host "STOPPED  (nothing listening on port $Port)" -ForegroundColor Yellow
    }
}

function Invoke-DockerRun {
    & docker run -d --rm -p "${Port}:${Port}" --name $DockerName $DockerImage
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    Write-Host "Container '$DockerName' on $BaseUrl. Logs: .\devctl.ps1 docker-logs" -ForegroundColor Green
}

function Show-Logs {
    if (-not (Test-Path $AppLog)) {
        Write-Host "No log yet at $AppLog. Start the server first (.\devctl.ps1 start)." -ForegroundColor Yellow
        return
    }
    if ($Since) {
        try { $sinceDt = [datetime]::Parse($Since) }
        catch {
            Write-Host "Invalid --since value: '$Since' (use 'yyyy-MM-dd HH:mm:ss')." -ForegroundColor Red
            exit 1
        }
        $printing = $false
        Get-Content -Path $AppLog | ForEach-Object {
            if (-not $printing) {
                $m = [regex]::Match($_, '^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3})')
                if ($m.Success -and ([datetime]::Parse($m.Groups[1].Value) -ge $sinceDt)) { $printing = $true }
            }
            if ($printing) { $_ }
        }
        return
    }
    if ($Follow -or -not $LinesExplicit) {
        Get-Content -Path $AppLog -Tail $Lines -Wait
    } else {
        Get-Content -Path $AppLog -Tail $Lines
    }
}

function Show-Help {
@"
devctl - developer control for the Flight Booking API

Usage: .\devctl.ps1 <command> [options]

Commands (long | short):
  start        | up     Start the server in the background (profile: dev)
  stop         | down   Stop the running server
  restart      | re     Restart the server
  status       | st     Show whether the server is running
  build        | b      Clean-compile and package the boot jar (skips tests)
  test         | t      Run the test suite
  zip          | z      Package sources/docs into dist/ (archiver)
  docker-build | db     Build the Docker image
  docker-run   | dr     Run the Docker image (detached) on the app port
  docker-stop  | dx     Stop the Docker container
  docker-logs  | dl     Follow the Docker container logs
  logs         | l      View / follow the application log
  open         | o      Open the Swagger UI in the default browser
  help         | h      Show this help

Options:
  -p, --profile <name>  Spring profile for start/restart (default: dev)
  -n, --lines   <N>     logs: show the last N lines (no follow)
  -s, --since   <ts>    logs: show lines from 'yyyy-MM-dd HH:mm:ss[.fff]'
  -f, --follow          logs: follow live output

Examples:
  .\devctl.ps1 start
  .\devctl.ps1 up -p prod
  .\devctl.ps1 re
  .\devctl.ps1 logs -f
  .\devctl.ps1 l -n 200
  .\devctl.ps1 logs --since "2026-07-08 12:00:00"
  .\devctl.ps1 db ; .\devctl.ps1 dr
"@ | Write-Host
}

# --- Dispatch (long + short aliases) ------------------------------------
switch ($Command.ToLowerInvariant()) {
    { $_ -in 'start', 'up' }               { Start-Server }
    { $_ -in 'stop', 'down' }              { Stop-Server }
    { $_ -in 'restart', 're' }             { Stop-Server; Start-Server }
    { $_ -in 'status', 'st' }              { Get-Status }
    { $_ -in 'build', 'b' }                { Invoke-Gradle @('clean', 'bootJar') }
    { $_ -in 'test', 't' }                 { Invoke-Gradle @('test') }
    { $_ -in 'zip', 'archive', 'z' }       { Invoke-Gradle @('runArchiver') }
    { $_ -in 'docker-build', 'db' }        { & docker build -t $DockerImage .; if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE } }
    { $_ -in 'docker-run', 'dr' }          { Invoke-DockerRun }
    { $_ -in 'docker-stop', 'dx' }         { & docker stop $DockerName }
    { $_ -in 'docker-logs', 'dl' }         { & docker logs -f $DockerName }
    { $_ -in 'logs', 'log', 'l' }          { Show-Logs }
    { $_ -in 'open', 'o' }                 { Start-Process "$BaseUrl/swagger-ui.html" }
    { $_ -in 'help', 'h', '-h', '--help', '/?' } { Show-Help }
    default { Write-Host "Unknown command: '$Command'`n" -ForegroundColor Red; Show-Help; exit 1 }
}
