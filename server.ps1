$Version = "1.20.1"
$Plugins = (
    "https://github.com/monun/auto-reloader/releases/download/0.0.2/AutoReloader.jar"
)
$Xms = "1G"
$Xmx = "1G"

function Create-Directory([string]$PathName) {
    if (!(Test-Path $PathName)) {
        New-Item $PathName -Type Directory | Out-Null
    }
}

function Download-File {
    [CmdletBinding()]
    Param (
        [string]$Url,
        [string]$Folder,
        [Parameter(Mandatory = $False)] [string]$Filename
    )
    Create-Directory $Folder
    $Location = (Get-Location).Path
    $Destfolder = Resolve-Path("$Location/$Folder")
    try {
        $WebRequest = [System.Net.HttpWebRequest]::Create($Url)
        $WebRequest.Method = "GET"
        $WebResponse = $WebRequest.GetResponse()
        if ( [string]::IsNullOrEmpty($Filename)) {
            $Disposition = $WebResponse.Headers['Content-Disposition']
            if ( [string]::IsNullOrEmpty($Disposition)) {
                $Filename = [System.IO.Path]::GetFileName($Url)
            } else {
                $Filename = [System.Net.Mime.ContentDisposition]::new($Disposition).FileName
            }
        }
        $Dest = "$Destfolder/$Filename"
        $FileInfo = [System.IO.FileInfo]$Dest
        if (Test-Path $Dest) {
            $RemoteLastModified = $WebResponse.LastModified
            $LocalLastModified = $FileInfo.LastWriteTime
            if ([datetime]::Compare($RemoteLastModified, $LocalLastModified) -eq 0) {
                Write-Host "UP-TO-DATE $Filename($Url)"
                $WebResponse.Dispose()
                return
            }
            Write-Host "Updating $Filename from $url"
        }
        else {
            Write-Host "Downloading $Filename from $url"
        }
        $ResponseStream = $WebResponse.GetResponseStream()
        $FileWriter = New-Object System.IO.FileStream ($Dest, [System.IO.FileMode]::Create)
        [byte[]]$buffer = New-Object byte[] 4096
        do {
            $length = $ResponseStream.Read($buffer, 0, 4096)
            $FileWriter.Write($buffer, 0, $length)
        } while ($length -ne 0)
        $ResponseStream.Close()
        $FileWriter.Close()
        $FileInfo.LastWriteTime = $WebResponse.LastModified
    }
    catch [System.Net.WebException] {
        $Status = $_.Exception.Response.StatusCode
        $Msg = $_.Exception
        Write-Host "Failed to dowloading $Dest, Status code: $Status - $Msg" -ForegroundColor Red
    }
}

function Choice {
    Param(
        [string]$Prompt,
        [string]$Choice,
        [char]$Default,
        [int]$Seconds
    )
    $Choice = $Choice.ToUpper()
    $StartTime = Get-Date
    $TimeOut = New-TimeSpan -Seconds $Seconds
    Write-Host $Prompt
    $Choose = $Default
    while ($CurrentTime -lt $StartTime + $TimeOut) {
        if ($host.UI.RawUI.KeyAvailable) {
            [string]$Key = ($host.UI.RawUI.ReadKey("IncludeKeyDown,NoEcho")).character
            $Key = $Key.ToUpper().ToCharArray()[0]
            if ( $Choice.Contains($Key)) {
                $Choose = $Key
                Break
            }
        }
        $CurrentTime = Get-Date
    }
    Write-Host $Choose
    return $Choose
}

Create-Directory ".server"
Set-Location ".server"
$host.UI.RawUI.WindowTitle = "PaperMC ($Version)"
Download-File "https://clip.aroxu.me/download?mc_version=$Version" "." "server.jar"
foreach ($Plugin in $Plugins) {
    Download-File $Plugin "plugins"
}
$JVMArgs = [System.Collections.ArrayList]@(
    "-Xms$Xms",
    "-Xmx$Xmx",
    "-Dcom.mojang.eula.agree=true",
    "-jar",
    "server.jar",
    "nogui"
)
while ($true) {
    java $JVMArgs
    $Restart = Choice "Restart? [Y] [N]" @('Y', 'N') 'Y' 1
    if ($Restart -eq 'N') {
        Set-Location "./.."
        break
    }
}