<#
.SYNOPSIS
    Checks whether https://www.xwizard.de is reachable and shows a desktop
    notification (and logs) when it goes down or recovers.

.NOTES
    Meant to run on a schedule (e.g. every 5 minutes via Task Scheduler) on
    this local Windows machine. Only alerts on state *changes* (healthy ->
    down, down -> healthy) so it doesn't spam a toast every run while an
    outage is ongoing; it does remind once per hour while still down.
#>

$ErrorActionPreference = 'Stop'

$Url        = 'https://www.xwizard.de/XWizard/Wizz?help'
$TimeoutSec = 15
$StateFile  = Join-Path $PSScriptRoot 'xwizard-monitor-state.json'
$LogFile    = Join-Path $PSScriptRoot 'xwizard-monitor.log'

function Write-Log([string]$Message) {
    $line = "{0}  {1}" -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $Message
    Add-Content -Path $LogFile -Value $line
}

function Show-Toast([string]$Title, [string]$Message) {
    try {
        [Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null
        $template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent(
            [Windows.UI.Notifications.ToastTemplateType]::ToastText02)
        $texts = $template.GetElementsByTagName('text')
        $texts.Item(0).AppendChild($template.CreateTextNode($Title)) | Out-Null
        $texts.Item(1).AppendChild($template.CreateTextNode($Message)) | Out-Null
        $toast = [Windows.UI.Notifications.ToastNotification]::new($template)
        [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('XWizard Monitor').Show($toast)
    } catch {
        # Toast APIs unavailable (e.g. running as SYSTEM) - fall back to a balloon tip.
        Add-Type -AssemblyName System.Windows.Forms
        $icon = New-Object System.Windows.Forms.NotifyIcon
        $icon.Icon = [System.Drawing.SystemIcons]::Warning
        $icon.Visible = $true
        $icon.ShowBalloonTip(10000, $Title, $Message, [System.Windows.Forms.ToolTipIcon]::Warning)
        Start-Sleep -Seconds 1
        $icon.Dispose()
    }
}

# --- Check the site ---
$isUp = $false
$detail = ''
try {
    $response = Invoke-WebRequest -Uri $Url -TimeoutSec $TimeoutSec -UseBasicParsing
    if ($response.StatusCode -eq 200 -and $response.RawContentLength -gt 1000) {
        $isUp = $true
        $detail = "HTTP $($response.StatusCode), $($response.RawContentLength) bytes"
    } else {
        $detail = "Unexpected response: HTTP $($response.StatusCode), $($response.RawContentLength) bytes"
    }
} catch {
    $detail = $_.Exception.Message
}

# --- Load previous state ---
$prevState = $null
if (Test-Path $StateFile) {
    try { $prevState = Get-Content $StateFile -Raw | ConvertFrom-Json } catch { $prevState = $null }
}

$now = Get-Date
$newState = [pscustomobject]@{
    IsUp         = $isUp
    LastChecked  = $now.ToString('o')
    LastDownSeen = if ($isUp) { $prevState.LastDownSeen } else { $now.ToString('o') }
    LastAlertedAt = $prevState.LastAlertedAt
}

if ($isUp) {
    Write-Log "UP - $detail"
    if ($prevState -and $prevState.IsUp -eq $false) {
        Show-Toast 'XWizard is back up' $detail
        Write-Log 'Recovery notification sent.'
    }
    $newState.LastAlertedAt = $null
} else {
    Write-Log "DOWN - $detail"
    $shouldAlert = $true
    if ($prevState -and $prevState.IsUp -eq $false -and $prevState.LastAlertedAt) {
        $lastAlert = [datetime]$prevState.LastAlertedAt
        if (($now - $lastAlert) -lt (New-TimeSpan -Hours 1)) {
            $shouldAlert = $false
        }
    }
    if ($shouldAlert) {
        Show-Toast 'XWizard appears to be DOWN' $detail
        $newState.LastAlertedAt = $now.ToString('o')
        Write-Log 'Down notification sent.'
    }
}

$newState | ConvertTo-Json | Set-Content -Path $StateFile
