param(
    [string]$ProxyUrl = $env:HTTPS_PROXY,
    [string]$MavenVersion = "3.9.9",
    [string]$TomcatVersion = "11.0.24"
)

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $scriptDir
$toolsDir = Join-Path $repoRoot ".tools"
$mavenHome = Join-Path $toolsDir "apache-maven-$MavenVersion"
$tomcatHome = Join-Path $toolsDir "apache-tomcat-$TomcatVersion"
$mavenZip = Join-Path $toolsDir "apache-maven-$MavenVersion-bin.zip"
$tomcatZip = Join-Path $toolsDir "apache-tomcat-$TomcatVersion.zip"

$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
$tomcatUrl = "https://dlcdn.apache.org/tomcat/tomcat-11/v$TomcatVersion/bin/apache-tomcat-$TomcatVersion.zip"

function Write-Step([string]$message) {
    Write-Host "[xwiz-setup] $message"
}

function Test-PortAvailable([int]$port) {
    try {
        $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $port)
        $listener.Start()
        $listener.Stop()
        return $true
    }
    catch {
        return $false
    }
}

function Get-AvailablePort([int]$startPort, [int]$endPort) {
    for ($port = $startPort; $port -le $endPort; $port++) {
        if (Test-PortAvailable -port $port) {
            return $port
        }
    }

    throw "No free TCP port found between $startPort and $endPort"
}

function Download-File([string]$url, [string]$destination, [string]$proxy) {
    $downloadParams = @{
        Uri = $url
        OutFile = $destination
    }

    if ($proxy) {
        $downloadParams.Proxy = $proxy
    }

    Invoke-WebRequest @downloadParams
}

if (-not (Test-Path $toolsDir)) {
    New-Item -ItemType Directory -Path $toolsDir | Out-Null
}

if (-not (Test-Path $mavenHome)) {
    Write-Step "Downloading Maven $MavenVersion"
    Download-File -url $mavenUrl -destination $mavenZip -proxy $ProxyUrl
    Write-Step "Extracting Maven"
    Expand-Archive -Path $mavenZip -DestinationPath $toolsDir -Force
}

if (-not (Test-Path $tomcatHome)) {
    Write-Step "Downloading Tomcat $TomcatVersion"
    Download-File -url $tomcatUrl -destination $tomcatZip -proxy $ProxyUrl
    Write-Step "Extracting Tomcat"
    Expand-Archive -Path $tomcatZip -DestinationPath $toolsDir -Force
}

$serverXmlPath = Join-Path $tomcatHome "conf/server.xml"
if (Test-Path $serverXmlPath) {
    $serverXml = Get-Content $serverXmlPath -Raw
    if ($serverXml -match '<Server port="8005"') {
        Write-Step "Switching Tomcat shutdown port from 8005 to 8006"
        $serverXml = $serverXml -replace '<Server port="8005"', '<Server port="8006"'
    }

    $currentHttpPort = 8080
    $portMatch = [regex]::Match($serverXml, '<Connector\s+port="(\d+)"\s+protocol="HTTP/1.1"')
    if ($portMatch.Success) {
        $currentHttpPort = [int]$portMatch.Groups[1].Value
    }

    if (-not (Test-PortAvailable -port $currentHttpPort)) {
        $newHttpPort = Get-AvailablePort -startPort 8081 -endPort 8099
        Write-Step "Port $currentHttpPort is busy. Switching Tomcat HTTP port to $newHttpPort"
        $portPattern = '<Connector\s+port="{0}"\s+protocol="HTTP/1.1"' -f $currentHttpPort
        $updatedServerXml = $serverXml -replace $portPattern, ('<Connector port="{0}" protocol="HTTP/1.1"' -f $newHttpPort)
        if ($updatedServerXml -eq $serverXml) {
            throw "Failed to update HTTP connector port in $serverXmlPath"
        }
        $serverXml = $updatedServerXml
        $currentHttpPort = $newHttpPort
    }

    Set-Content -Path $serverXmlPath -Value $serverXml -Encoding UTF8

    $urlPath = Join-Path $toolsDir "xwiz-localhost-url.txt"
    Set-Content -Path $urlPath -Value "http://localhost:$currentHttpPort/XWizard/Wizz?help" -Encoding ASCII
}

$javaCommand = Get-Command java -ErrorAction Stop
$javaBinDir = Split-Path -Parent $javaCommand.Source
$env:JAVA_HOME = Split-Path -Parent $javaBinDir
$env:PATH = "$mavenHome\bin;$env:PATH"
$env:MAVEN_OPTS = "-Djavax.net.ssl.trustStoreType=Windows-ROOT"

Push-Location $repoRoot
try {
    Write-Step "Building XWizard WAR with Maven"
    & "$mavenHome\bin\mvn.cmd" -U clean package
}
finally {
    Pop-Location
}

$warPath = Join-Path $repoRoot "target/XWizard.war"
if (-not (Test-Path $warPath)) {
    throw "Build did not produce target/XWizard.war"
}

$webappsDir = Join-Path $tomcatHome "webapps"
if (-not (Test-Path $webappsDir)) {
    throw "Tomcat webapps directory not found: $webappsDir"
}

Write-Step "Deploying WAR to Tomcat"
Copy-Item -Path $warPath -Destination (Join-Path $webappsDir "XWizard.war") -Force

$explodedDir = Join-Path $webappsDir "XWizard"
if (Test-Path $explodedDir) {
    Remove-Item -Path $explodedDir -Recurse -Force
}

Write-Step "Setup complete"
Write-Host "Tomcat home: $tomcatHome"
if (Test-Path (Join-Path $toolsDir "xwiz-localhost-url.txt")) {
    $effectiveUrl = (Get-Content (Join-Path $toolsDir "xwiz-localhost-url.txt") -Raw).Trim()
    Write-Host "URL: $effectiveUrl"
}
Write-Host "Run next: .\scripts\start-xwiz-localhost.ps1"
