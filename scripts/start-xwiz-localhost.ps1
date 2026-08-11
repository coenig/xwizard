$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $scriptDir
$tomcatHome = Join-Path $repoRoot ".tools/apache-tomcat-11.0.24"

if (-not (Test-Path $tomcatHome)) {
    throw "Tomcat is not installed at $tomcatHome. Run .\scripts\setup-xwiz-localhost.ps1 first."
}

$javaCommand = Get-Command java -ErrorAction Stop
$javaBinDir = Split-Path -Parent $javaCommand.Source
$env:JAVA_HOME = Split-Path -Parent $javaBinDir
$env:CATALINA_HOME = $tomcatHome

$startupScript = Join-Path $tomcatHome "bin/startup.bat"
if (-not (Test-Path $startupScript)) {
    throw "Could not find $startupScript"
}

$serverXmlPath = Join-Path $tomcatHome "conf/server.xml"
$httpPort = 8080
if (Test-Path $serverXmlPath) {
    $serverXml = Get-Content $serverXmlPath -Raw
    $portMatch = [regex]::Match($serverXml, '<Connector\s+port="(\d+)"\s+protocol="HTTP/1.1"')
    if ($portMatch.Success) {
        $httpPort = [int]$portMatch.Groups[1].Value
    }
}

& $startupScript

Write-Host "XWizard start requested."
Write-Host "Open: http://localhost:$httpPort/XWizard/Wizz?help"
