$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $scriptDir
$tomcatHome = Join-Path $repoRoot ".tools/apache-tomcat-11.0.24"

if (-not (Test-Path $tomcatHome)) {
    throw "Tomcat is not installed at $tomcatHome."
}

$javaCommand = Get-Command java -ErrorAction Stop
$javaBinDir = Split-Path -Parent $javaCommand.Source
$env:JAVA_HOME = Split-Path -Parent $javaBinDir
$env:CATALINA_HOME = $tomcatHome

$shutdownScript = Join-Path $tomcatHome "bin/shutdown.bat"
if (-not (Test-Path $shutdownScript)) {
    throw "Could not find $shutdownScript"
}

& $shutdownScript
Write-Host "XWizard stop requested."
